package it.unige.diten.dsp.speakerrecognition;

import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import it.unige.diten.dsp.speakerrecognition.libsvm.*;


public abstract class MySVM
{
    private static svm_model model = null;
    public static final String TAG = "MySVM";
    public static int[] results;

    private static boolean initialized = false;
    private static double[] y_min;
    private static double[] y_max;

    private static void initialize()
    {
        // TODO correggi 26 con il numero delle feature estratte
        y_min = new double[26];
        y_max = new double[26];
        Log.v(TAG, "MySVM: fileName: " + MainActivity.MODEL_FILENAME);

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

    public static int RecognizeSpeaker() {
        if (!initialized) {
            initialize();
            initialized = true;
        }

        // TODO fill "features"
        scaleMatrix(FeatureExtractor.MFCC);
        scaleMatrix(FeatureExtractor.DeltaDelta);
        int frameCount = FeatureExtractor.MFCC.length;
        svm_node[][] features = new svm_node[frameCount][FeatureExtractor.MFCC_COUNT * 2 + 1];
        for (int F = 0; F < frameCount; F++) {
            int C;
            for (C = 0; C < FeatureExtractor.MFCC_COUNT; C++) {
                features[F][C] = new svm_node();
                features[F][C].index = C + 1;
                features[F][C].value =  FeatureExtractor.MFCC[F][C];
            }

            for (; C < FeatureExtractor.MFCC_COUNT * 2; C++) {
                features[F][C] = new svm_node();
                features[F][C].index = C + 1;
                features[F][C].value = FeatureExtractor.DeltaDelta[F][C - FeatureExtractor.MFCC_COUNT];
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
        results = new int[3];
        for(int i=0; i<3; i++)
            results[i] = 0;

        //double[] allResults = new double[frameCount];

        //svm_node[][] dummy = new svm_node[2][3];

        /*dummy[0][0] = new svm_node();
        dummy[0][0].index = 1;
        dummy[0][0].value = 1;
        dummy[0][1] = new svm_node();
        dummy[0][1].index = 2;
        dummy[0][1].value = -3;
        dummy[0][2] = new svm_node();
        dummy[0][2].index = -1;

        dummy[1][0] = new svm_node();
        dummy[1][0].index = 1;
        dummy[1][0].value = -1;
        dummy[1][1] = new svm_node();
        dummy[1][1].index = 2;
        dummy[1][1].value = 3;
        dummy[1][2] = new svm_node();
        dummy[1][2].index = -1;

        Log.i(TAG,"res(1) = " + (int)svm.svm_predict(model, dummy[0]));
        Log.i(TAG,"res(2) = " + (int)svm.svm_predict(model, dummy[1]));*/


        int res;
        for(int F = 0; F < frameCount; F++)
        {
            res = (int)svm.svm_predict(model, features[F]);
            results[res]++;
        }

        Log.i(TAG, "Res(0): " + results[0]);
        Log.i(TAG, "Res(1): " + results[1]);
        Log.i(TAG, "Res(2): " + results[2]);

        // Find the most popular outcome
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

        return maxI;
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
}
