package it.unige.diten.dsp.speakerrecognition;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import it.unige.diten.dsp.speakerrecognition.libsvm.svm;
import it.unige.diten.dsp.speakerrecognition.libsvm.svm_model;
import it.unige.diten.dsp.speakerrecognition.libsvm.svm_node;

public class MySVM_Async extends AsyncTask<Void, Integer, Void>
{
    private static svm_model model = null;
    public static final String TAG = "MySVM_Async";

    private static ProgressDialog cProgressRecorder;
    private static Context cContext;

    private static boolean initialized = false;
    private static double[] y_min;
    private static double[] y_max;

    private static void initialize()
    {
        //cProgressRecorder.setMessage("Loading SVM model and features range...");
        // TODO correggi 26 con il numero delle feature estratte
        y_min = new double[26];
        y_max = new double[26];
        Log.v(TAG, "fileName: " + MainActivity.MODEL_FILENAME);

        readRange(MainActivity.RANGE_FILENAME);

        Log.v(TAG, "Caricato range.range");

        try
        {
            model = svm.svm_load_model(MainActivity.MODEL_FILENAME);

            Log.v(TAG, "Caricato model.model");
        }
        catch(IOException ew)
        {
            Log.e(TAG, "initialize: " + ew.getMessage());
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

        // TODO fill "features"
        // feature matrix
        double[][] allFeatures = new double[frameCount][FeatureExtractor.MFCC_COUNT*2];
        //scaleMatrix(FeatureExtractor.MFCC);
        //scaleMatrix(FeatureExtractor.DeltaDelta);
        // Unite the two matrices
        for(int C = 0; C < frameCount; C++)
        {
            int K = 0;
            for(K = 0; K < FeatureExtractor.MFCC_COUNT; K++ )
                allFeatures[C][K] = FeatureExtractor.MFCC[C][K];

            for(; K < FeatureExtractor.MFCC_COUNT * 2; K++)
                allFeatures[C][K] = FeatureExtractor.DeltaDelta[C][K-FeatureExtractor.MFCC_COUNT];
        }
        scaleMatrix(allFeatures);


        svm_node[][] features = new svm_node[frameCount][FeatureExtractor.MFCC_COUNT * 2 + 1];
        for (int F = 0; F < frameCount; F++)
        {
            int C;
            for(C = 0; C < FeatureExtractor.MFCC_COUNT*2; C++) {
                features[F][C] = new svm_node();
                features[F][C].value = allFeatures[F][C];
                features[F][C].index = C+1;
            }
            // Last but not least.
            features[F][C] = new svm_node();
            features[F][C].index = -1;
            features[F][C].value = 0;
        }



        /*for (int i = 0; i < features.length; i++){ //feats
            for (int j = 0; j < features[0].length; j++) {
                Log.i(TAG, "FEAT - index[" + i + "][" + j + "]: " + features[i][j].index);
                Log.i(TAG, "FEAT - value[" + i + "][" + j + "]: " + features[i][j].value);
            }
        }*/

        int[] results = new int[3];
        for(int i=0; i<3; i++)
            results[i] = 0;


        // Multithreaded recognition!
        publishProgress(3);
        Runnable[] runnables = new Runnable[MainActivity.numCores];
        Thread[] threads = new Thread[MainActivity.numCores];
        for(int C = 0; C < MainActivity.numCores; C++)
        {
            runnables[C] = new RecognitionThread(C,MainActivity.numCores,features,results,model);
            threads[C] = new Thread(runnables[C]);
            threads[C].start();
        }
        try {
            for (int C = 0; C < MainActivity.numCores; C++)
                threads[C].join();
        }
        catch(Exception ewww)
        {
            // TODO handle exceptions
        }

        MainActivity.SVMResults  = results;

        Log.i(TAG, "Res(0): " + results[0]);
        Log.i(TAG, "Res(1): " + results[1]);
        Log.i(TAG, "Res(2): " + results[2]);

        // Find most popular outcome
        int maxV = -1;
        int maxI = -1;
        for(int C = 0; C < 3; C++)
        {
            if(results[C] > maxV)
            {
                maxI = C;
                maxV = results[C];
            }
        }

        RecognitionReceiver.result = maxI;

        Intent intent = new Intent("it.unige.diten.dsp.speakerrecognition.UPDATE_RECOGNITION");
        cContext.sendBroadcast(intent);

        return null;
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

    private static void scaleMatrix(double[][] input)
    {
        double y_lower = -1;
        double y_upper = 1;

        for(int C = 0; C < input.length; C++)
        {
            for (int J = 0; J < input[0].length; J++)
            {
                input[C][J] = y_lower + (y_upper - y_lower) * (input[C][J] - y_min[J]) / (y_max[J] - y_min[J]);
            }
        }
    }

    private static void readRange(String fileName)
    {
        BufferedReader br = null;

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(fileName));
            int lineNumber = 1;
            while ((sCurrentLine = br.readLine()) != null)
            {
                Log.v(TAG,"readRange: line read '" + sCurrentLine + "'");
                if(lineNumber >= 3)
                {
                    String[] arr = sCurrentLine.split(" ");
                    y_min[lineNumber - 3] = Double.valueOf(arr[1]);
                    y_max[lineNumber - 3] = Double.valueOf(arr[2]);

                    Log.v(TAG, "y_min[" + (lineNumber - 3) + "] = " + y_min[lineNumber - 3]);
                    Log.v(TAG, "y_max[" + (lineNumber - 3) + "] = " + y_max[lineNumber - 3]);
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
