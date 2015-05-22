package it.unige.diten.dsp.speakerrecognition;


import android.os.AsyncTask;

public class FeatureExtractor extends AsyncTask <String, Void, Boolean> {

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
            // Periodogram

            // Mel scaling with filters of periodogram

            // Logarithm energies

            // DCT

            // Delta-deltas

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
