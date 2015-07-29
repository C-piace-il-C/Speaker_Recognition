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

public class MySVM_Async extends AsyncTask<Void, Integer, Void>
{
    public static final String  TAG                 = "MySVM_Async";
    public static final int     modelCount          = 3;
    private static svm_model[]  models              = null;
    private static final int    decisionAlgorithm   = 1;

    private static ProgressDialog cProgressRecorder;
    private static Context cContext;

    private static boolean initialized = false;

    private static Range[] ranges;

    private static void initialize()
    {
        models = new svm_model[modelCount];

        ranges = new Range[modelCount];
        for(int C=0; C<modelCount; C++)
            ranges[C] = new Range();

        // Search for and use the first .model and .range files
        String   path            = MainActivity.PATH;
        File     f               = new File(path);
        File     files[]         = f.listFiles();
        String[] modelFilenames  = new String[modelCount];
        String[] rangeFilenames  = new String[modelCount];
        int      modelsFound     = 0;

        for (File file : files) {
            String filename = file.getName();
            if (filename.endsWith(".model")) {
                modelFilenames[modelsFound] = path + "/" + filename;
                modelsFound++;
                if (modelsFound == modelCount)
                    break;
            }
        }

        if(modelsFound < modelCount)
        {
            Log.e(TAG, "Not enough models were found. Some are missing.");
            return;
        }

        try
        {
            for(int C = 0; C < modelCount; C++)
            {
                rangeFilenames[C] = modelFilenames[C].replace(".model",".range");
                readRange(rangeFilenames[C], ranges[C]);
                models[C] = svm.svm_load_model(modelFilenames[C]);
            }
        }
        catch(IOException ew)
        {
            ew.printStackTrace();
        }
    }

    protected void onPreExecute()
    {
        super.onPreExecute();

        cContext = MainActivity.context;

        cProgressRecorder = new ProgressDialog(cContext);
        cProgressRecorder.setIndeterminate(true);
        cProgressRecorder = ProgressDialog.show(cContext, "Recognition", "recognition in progress...");
    }

    @Override
    protected Void doInBackground(Void...params)
    {
        if (!initialized)
        {
            publishProgress(2);
            initialize();
            initialized = true;
        }
        publishProgress(1);
        // fill features vectors (svm_node[][])
        int frameCount = FeatureExtractor.MFCC.length;

        // feature matrix
        double[][][] allFeatures = new double[modelCount][frameCount][FeatureExtractor.MFCC_COUNT*2];
        // Unite the two matrices
        for(int M = 0; M < modelCount; M++) {
            for (int C = 0; C < frameCount; C++) {
                int K;
                for (K = 0; K < FeatureExtractor.MFCC_COUNT; K++)
                    allFeatures[M][C][K] = FeatureExtractor.MFCC[C][K];

                for (; K < FeatureExtractor.MFCC_COUNT * 2; K++)
                    allFeatures[M][C][K] = FeatureExtractor.DeltaDelta[C][K - FeatureExtractor.MFCC_COUNT];
            }
            scaleMatrix(allFeatures[M], ranges[M]);
        }


        svm_node[][][] features = new svm_node[modelCount][frameCount][FeatureExtractor.MFCC_COUNT * 2 + 1];
        for(int M = 0; M < modelCount; M++)
        {
            for (int F = 0; F < frameCount; F++) {
                int C;
                for (C = 0; C < FeatureExtractor.MFCC_COUNT * 2; C++) {
                    features[M][F][C] = new svm_node();
                    features[M][F][C].value = allFeatures[M][F][C];
                    features[M][F][C].index = C + 1;
                }
                features[M][F][C] = new svm_node();
                features[M][F][C].index = -1;
                features[M][F][C].value = 0;
            }
        }
        // Create results matrix and initialize to zero
        int[][] results = new int[modelCount][3];
        for(int i=0; i<modelCount; i++)
            for( int j=0; j<3; j++)
                results[i][j] = 0;


        // Multithreaded recognition
        publishProgress(3);
        try
        {
            int numCores = MainActivity.numCores;

            Thread[] threads = new Thread[numCores];
            for(int C = 0; C < numCores; C++)
            {
                threads[C] = new Thread(new RecognitionThread(C, numCores, features, results, models));
                threads[C].start();
            }

            // Pause the current thread until all threads are done.
            for (int C = 0; C < numCores; C++)
                threads[C].join();
        }
        catch(Exception ew)
        {
            ew.printStackTrace();
        }
        

        // Pass a parameter to the intent receiver before sending the intent
        RecognitionReceiver.result = decideResult(results);
        Intent intent = new Intent("it.unige.diten.dsp.speakerrecognition.UPDATE_RECOGNITION");
        cContext.sendBroadcast(intent);

        return null;
    }

    // Decides and returns the speaker (0,1,2)
    private static int decideResult( int[][] results )
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
            for(int M=0;M<modelCount;M++)
                for(int L=0;L<3;L++)
                    if(results[M][L] > mostSecureValue)
                    {
                        mostSecureValue = results[M][L];
                        mostSecureModel = M;
                    }

            MainActivity.SVMResults = results[mostSecureModel];

            // detect most popular recognized label
            int mostPopularLabel = -1;
            int mostPopularValue = -1;
            for(int L=0;L<3;L++)
                if(results[mostSecureModel][L] > mostPopularValue)
                {
                    mostPopularValue = results[mostSecureModel][L];
                    mostPopularLabel = L;
                }


            return(mostPopularLabel);

        default:
            // unrecognized algorithm
            MainActivity.SVMResults = results[0];
            // detect most popular recognized label
            int mpl = -1;
            int mpv = -1;
            for(int L=0;L<3;L++)
                if(results[0][L] > mpv)
                {
                    mpv = results[0][L];
                    mpl = L;
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

    private static void scaleMatrix(double[][] input, Range range)
    {
        double y_lower = -1;
        double y_upper = 1;

        for(int C = 0; C < input.length; C++)
        {
            for (int J = 0; J < input[0].length; J++)
            {
                input[C][J] = y_lower + (y_upper - y_lower) * (input[C][J] - range.y_min[J]) / (range.y_max[J] - range.y_min[J]);
            }
        }
    }

    /// reads the minimum and maximum values from filename and stores them into rangePtr
    private static void readRange(String fileName, Range rangePtr)
    {
        BufferedReader br = null;

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(fileName));
            int lineNumber = 1;
            while ((sCurrentLine = br.readLine()) != null)
            {
                //Log.v(TAG,"readRange: line read '" + sCurrentLine + "'");
                if(lineNumber >= 3)
                {
                    String[] arr = sCurrentLine.split(" ");

                    rangePtr.y_min[lineNumber - 3] = Double.valueOf(arr[1]);
                    rangePtr.y_max[lineNumber - 3] = Double.valueOf(arr[2]);

                    //Log.v(TAG, "y_min[" + (lineNumber - 3) + "] = " + rangePtr.y_min[lineNumber - 3]);
                    //Log.v(TAG, "y_max[" + (lineNumber - 3) + "] = " + rangePtr.y_max[lineNumber - 3]);
                }
                lineNumber++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    @Override
    protected void onPostExecute(Void cv)
    {
        super.onPostExecute(cv);
        cProgressRecorder.dismiss();
    }
}
