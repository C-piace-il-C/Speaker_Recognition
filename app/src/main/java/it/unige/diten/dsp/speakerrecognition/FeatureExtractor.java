package it.unige.diten.dsp.speakerrecognition;

// TODO FeatureExtractor: intent.

import android.os.AsyncTask;

import java.io.FileWriter;
import java.io.IOException;

public class FeatureExtractor extends AsyncTask <String, Void, Boolean> {

    public final static int MFCC_COUNT = 13;

    @Override
    protected void onPreExecute() {
        //nein
    }

    @Override
    protected Boolean doInBackground(String... params) {
        // Params[0] = filename of the file from which to extract the features
        try {
            // Framing
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
            String outputFileName = params[0].replace(".wav",".ff");

            printFeatureFile(outputFileName, MFCC, DeltaDelta);

        }
        catch(Exception ew)
        {
            return(Boolean.FALSE);
        }


        return(Boolean.TRUE);
    }

    @Override
    protected void onPostExecute(Boolean cv)
    {

    }


    private void printFeatureFile(String fileName, double[][] MFCC, double [][] DeltaDelta)
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
