package it.unige.diten.dsp.speakerrecognition;


import android.os.AsyncTask;

import it.unige.diten.dsp.speakerrecognition.Logarithmer;

public class FeatureExtractor extends AsyncTask <String, Void, Boolean> {

    private final static int MFCC_COUNT = 13;

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
            double[][] mfcc = new double[frames.length][];
            for(int frame = 0; frame < frames.length; frame++)
            {
                mfcc[frame] = DCT.computeDCT(
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
            double[][] delta_delta = DD.computeDD(mfcc, 2);

            /**
             * Save to file
             * save with the same filename of the wav with a different extension:
             * ff = feature file
             */
            String outputFileName = params[0].replace(".wav",".ff");
        }
        catch(Exception ew)
        {
            return(Boolean.FALSE);
        }


        return(Boolean.TRUE);
    }

    @Override
    protected void onPostExecute(Boolean cv) {
        //nein
    }

}
