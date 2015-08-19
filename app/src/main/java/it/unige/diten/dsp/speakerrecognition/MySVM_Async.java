package it.unige.diten.dsp.speakerrecognition;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import it.unige.diten.dsp.speakerrecognition.libsvm.svm;
import it.unige.diten.dsp.speakerrecognition.libsvm.svm_model;
import it.unige.diten.dsp.speakerrecognition.libsvm.svm_node;

/**
 * SVM Recognition in background.
 */
public class MySVM_Async extends AsyncTask<Void, Integer, Void>
{
    public static final String    TAG                 = "MySVM_Async";
    public static final int modelCnt = 1;
    private static svm_model[]    models              = null;
    private static final int      decisionAlgorithm   = 1;

    private static ProgressDialog cProgressRecorder;
    private static Context        cContext;

    private static boolean        initialized         = false;

    private static Range[]        ranges;

    private static void initialize()
    {
        models = new svm_model[modelCnt];

        ranges = new Range[modelCnt];
        for (int i = 0; i < modelCnt; i++)
        {
            ranges[i] = new Range(FeatureExtractor.MFCC_COUNT * 2);
        }

        File     folder          = new File(MainActivity.PATH);
        File     files[]         = folder.listFiles();
        String[] modelFilenames  = new String[modelCnt];
        String[] rangeFilenames  = new String[modelCnt];
        int      modelsFound     = 0;

        // Search for and use the first .model and .range files
        for (File file : files)
        {
            String fileName = file.getName();

            if (fileName.endsWith(".model"))
            {
                modelFilenames[modelsFound] = MainActivity.PATH + "/" + fileName;
                modelsFound++;
                if (modelsFound == modelCnt) { break; }
            }
        }

        if (modelsFound < modelCnt)
        {
            Log.e(TAG, "Not enough models were found. Some are missing.");
            return;
        }

        try
        {
            for (int i = 0; i < modelCnt; i++)
            {
                rangeFilenames[i] = modelFilenames[i].replace(".model", ".range");
                readRange(rangeFilenames[i], ranges[i]);
                models[i] = svm.svm_load_model(modelFilenames[i]);
            }
        }
        catch (IOException ew) { ew.printStackTrace(); }
    }

    protected void onPreExecute()
    {
        super.onPreExecute();

        cContext = MainActivity.context;

        cProgressRecorder = new ProgressDialog(cContext);
        cProgressRecorder.setIndeterminate(true);
        cProgressRecorder = ProgressDialog.show(cContext, "Recognition", "Recognizing");
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        if (!initialized)
        {
            publishProgress(2);
            initialize();
            initialized = true;
        }
        publishProgress(1);

        // Fill features vectors (svm_node[][]).
        int frameCnt = FeatureExtractor.MFCC.length;

        // Feature Matrix.
        double[][][] allFeatures = new double[modelCnt][frameCnt][FeatureExtractor.MFCC_COUNT*2];

        // Merge the two matrices.
        for(int M = 0; M < modelCnt; M++)
        {
            for (int C = 0; C < frameCnt; C++)
            {
                int K;

                for (K = 0; K < FeatureExtractor.MFCC_COUNT; K++)
                {
                    allFeatures[M][C][K] = FeatureExtractor.MFCC[C][K];
                }

                for ( ; K < FeatureExtractor.MFCC_COUNT * 2; K++)
                {
                    allFeatures[M][C][K] =
                            FeatureExtractor.DeltaDelta[C][K - FeatureExtractor.MFCC_COUNT];
                }
            }

            scaleMatrix(allFeatures[M], ranges[M]);
        }

        svm_node[][][] features = new svm_node[modelCnt][frameCnt][FeatureExtractor.MFCC_COUNT * 2 + 1];

        // Setup svm_node matrix.
        for(int M = 0; M < modelCnt; M++)
        {
            for (int F = 0; F < frameCnt; F++)
            {
                int C;

                for (C = 0; C < FeatureExtractor.MFCC_COUNT * 2; C++)
                {
                    features[M][F][C]       = new svm_node();
                    features[M][F][C].value = allFeatures[M][F][C];
                    features[M][F][C].index = C + 1;
                }

                features[M][F][C]       = new svm_node();
                features[M][F][C].index = -1;
                features[M][F][C].value = 0;
            }
        }

        // Create results matrix and initialize to zero.
        int[][] results = new int[modelCnt][3];
        for(int i = 0; i < modelCnt; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                results[i][j] = 0;
            }
        }

        // Multithreaded recognition
        publishProgress(3);
        try
        {
            Thread[] threads = new Thread[MainActivity.numCores];

            for(int i = 0; i < MainActivity.numCores; i++)
            {
                threads[i] = new Thread(
                        new RecognitionThread(i, MainActivity.numCores, features, results, models)
                        );
                threads[i].start();
            }

            // Pause the current thread until all threads are done.
            for (Thread t : threads) { t.join(); }
        }
        catch(Exception ew) { ew.printStackTrace(); }

        // Pass a parameter to the intent receiver before sending the intent.
        RecognitionReceiver.result = decideResult(results);
        Intent intent = new Intent("it.unige.diten.dsp.speakerrecognition.UPDATE_RECOGNITION");
        cContext.sendBroadcast(intent);

        return null;
    }

    // Decides and returns the speaker (0, 1, 2)
    private static int decideResult(int[][] results)
    {
        // results[Model][Label] = number of frames recognized as Label from Model
        // after deciding the speaker label you have to update
        // MainActivity.SVMResults (int[3] used by the pie chart)
        switch(decisionAlgorithm)
        {
        case(1):
            // detect most secure model
            int mostSecureModel = -1;
            int mostSecureValue = -1;
            for(int i = 0; i < modelCnt; i++)
            {
                for (int j = 0; j < 3; j++)
                {
                    if (results[i][j] > mostSecureValue)
                    {
                        mostSecureValue = results[i][j];
                        mostSecureModel = i;
                    }
                }
            }

            MainActivity.SVMResults = results[mostSecureModel];

            // Detect most popular recognized label.
            int mostPopularLabel = -1;
            int mostPopularValue = -1;

            for(int i = 0; i < 3; i++)
            {
                if (results[mostSecureModel][i] > mostPopularValue)
                {
                    mostPopularValue = results[mostSecureModel][i];
                    mostPopularLabel = i;
                }
            }

            return(mostPopularLabel);

        default:
            // Unrecognized algorithm.
            MainActivity.SVMResults = results[0];
            // Detect most popular recognized label.
            int mpl = -1;
            int mpv = -1;

            for(int i = 0; i < 3; i++)
            {
                if (results[0][i] > mpv)
                {
                    mpv = results[0][i];
                    mpl = i;
                }
            }

            return(mpl);
        }
    }
    @Override
    protected void onProgressUpdate(Integer...params)
    {
        switch(params[0])
        {
            case(1):
                cProgressRecorder.setMessage("Building features matrix...");
                break;
            case(2):
                cProgressRecorder.setMessage("Loading SVM model and range...");
                break;
            case(3):
                cProgressRecorder.setMessage("Speaker recognition...");
                break;
            default:
                cProgressRecorder.setMessage("Unknown event");
                break;
        }
    }

    /**
     * Scale matrix given range.
     * @param in    Matrix to be scaled.
     * @param r     Range to be used.
     */
    private static void scaleMatrix(double[][] in, Range r)
    {
        // y_Lower and y_Higher.
        double yL = -1;
        double yH = 1;

        for(int i = 0; i < in.length; i++)
        {
            for (int j = 0; j < in[0].length; j++)
            {
                in[i][j] = yL + (yH - yL) * (in[i][j] - r.y_min[j]) / (r.y_max[j] - r.y_min[j]);
            }
        }
    }

    /**
     * Read minimum and maximum values from fileName and store them into range.
     * @param fileName  Path to file to be read.
     * @param range     Destination.
     */
    private static void readRange(String fileName, Range range)
    {
        BufferedReader br = null;

        try
        {
            br = new BufferedReader(new FileReader(fileName));

            int     lineCnt     = 1;
            String  currentLine = br.readLine();

            while (currentLine != null)
            {
                //Log.v(TAG,"readRange: line read '" + currentLine + "'");
                if(lineCnt >= 3)
                {
                    String[] arr = currentLine.split(" ");

                    range.y_min[lineCnt - 3] = Double.valueOf(arr[1]);
                    range.y_max[lineCnt - 3] = Double.valueOf(arr[2]);

                    //Log.v(TAG, "y_min[" + (lineCnt - 3) + "] = " + range.y_min[lineCnt - 3]);
                    //Log.v(TAG, "y_max[" + (lineCnt - 3) + "] = " + range.y_max[lineCnt - 3]);
                }

                currentLine = br.readLine();
                lineCnt++;
            }
        }
        catch (IOException e) { e.printStackTrace(); }
        finally
        {
            try
            {
                if (br != null) { br.close(); }
            }
            catch (IOException ex) { ex.printStackTrace(); }
        }
    }

    @Override
    protected void onPostExecute(Void cv)
    {
        super.onPostExecute(cv);
        cProgressRecorder.dismiss();
    }
}
