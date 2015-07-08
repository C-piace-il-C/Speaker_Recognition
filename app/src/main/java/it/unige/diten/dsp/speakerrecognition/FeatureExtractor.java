package it.unige.diten.dsp.speakerrecognition;

// TODO FeatureExtractor: intent.

import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileWriter;
import java.io.IOException;

public class FeatureExtractor extends AsyncTask <String, Void, Boolean> {

    public final static int MFCC_COUNT = 13;
    public final static String TAG = "FeatureExtractor";

    @Override
    protected void onPreExecute() {
        // Nein
    }

    @Override
    protected Boolean doInBackground(String... params) {
        // Params[0] = the file's name of the file from which to extract the features
        Log.i (TAG, TAG + " STARTED!");
        try {
            // Framing
            Log.v (TAG, "params[0]: " + params[0]);
            Framer.readFromFile(params[0]);
            Frame[] frames = Framer.getFrames();


            // MFCCs
            double[][] MFCC = new double[frames.length][];
            for(int frame = 0; frame < frames.length; frame++)
            {
                MFCC[frame] = DCT.computeDCT(
                        Logarithmer.computeLogarithm(
                                MelScaler.extractMelEnergies(
                                        Periodogrammer.computePeriodogram(
                                                frames[frame]
                                        )
                                )
                        ), MFCC_COUNT // Only keep the first MFCC_COUNT coefficients of the resulting DCT sequence
                );
            }

            // Delta-deltas
            double[][] DeltaDelta = DD.computeDD_0(MFCC, 2);

            /**
             * Save to file
             * save with the same filename of the wav with a different extension:
             * ff = feature file
             */
            String outputFileName = params[0].replace(MainActivity.AUDIO_EXT, MainActivity.FEATURE_EXT);
            Log.v (TAG, "outputFileName: " + outputFileName);
            writeFeatureFile(outputFileName, MFCC, DeltaDelta);

        }
        catch(Exception ew)
        {
            Log.e (TAG, ew.getMessage());
            return(Boolean.FALSE);
        }

        Log.i(TAG, TAG + " ENDED! (the file's name is in the folder)");

        return(Boolean.TRUE);
    }

    @Override
    protected void onPostExecute(Boolean cv)
    {

    }


    private void writeFeatureFile(String fileName, double[][] MFCC, double[][] DeltaDelta)
    {
        // Stampo su file testuale i vettori
        try {

            FileWriter writer = new FileWriter(fileName, true);

            /*
            * output:
            * %MFCC                             %DD
            * MFCC0     MFCC1   MFCC2   ...     DD0     DD1
            * frame1
            * frame2
            * .
            * .
            * .
            */

            for(int f = 0; f < Framer.getFrames().length; f++)
            {
                String line = "";

                for (int k = 0; k < MFCC_COUNT; k++)
                {
                    line += Double.toString(MFCC[f][k]) + "\t";
                }


                for (int k = 0; k < DD.DD_COUNT; k++)
                {
                    line += Double.toString(DeltaDelta[f][k]) + "\t";
                }

                line = line.substring(0, line.length() - 2) + "\r\n";

                writer.write(line);
            }
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
