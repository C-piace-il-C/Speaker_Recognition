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

        try
        {
            model = svm.svm_load_model(MainActivity.MODEL_FILENAME);
        }
        catch(IOException ew)
        {
            Log.e(TAG, "initialize: " + ew.getMessage());
        }
    }

    public static int RecognizeSpeaker()
    {
        if(!initialized)
        {
            initialize();
            initialized = true;
        }

        // TODO fill "features"
        scaleMatrix(FeatureExtractor.MFCC);
        scaleMatrix(FeatureExtractor.DeltaDelta);
        int frameCount = FeatureExtractor.MFCC.length;
        svm_node[][] features = new svm_node[frameCount][FeatureExtractor.MFCC_COUNT * 2 + 1];
        for(int F = 0; F < frameCount; F++)
        {
            int C;
            for(C = 0; C < FeatureExtractor.MFCC_COUNT; C++)
            {
                features[F][C] = new svm_node();
                features[F][C].index = C + 1;
                features[F][C].value = FeatureExtractor.MFCC[F][C];
            }
            for(; C < FeatureExtractor.MFCC_COUNT * 2; C++)
            {
                features[F][C] = new svm_node();
                features[F][C].index = C + 1;
                features[F][C].value = FeatureExtractor.DeltaDelta[F][C - FeatureExtractor.MFCC_COUNT];
            }
            // Last but not least.
            features[F][C] = new svm_node();
            features[F][C].index = -1;
            features[F][C].value = Double.NaN;
        }

        int[] results = new int[3];
        for(int C = 0; C < features.length; C++)
        {
            int luxatepls = (int)svm.svm_predict(model, features[C]);
            results[luxatepls]++;
        }

        int maxV = -1;
        int maxIbon = -1;

        for(int C = 0; C < 3; C++)
        {
            if(results[C] > maxV)
            {
                maxIbon = C;
                maxV = results[C];
            }
        }

        return maxIbon;
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
