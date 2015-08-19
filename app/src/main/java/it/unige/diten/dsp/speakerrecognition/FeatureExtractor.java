package it.unige.diten.dsp.speakerrecognition;

// TODO: Eseguire nuovo database di registrazioni da un telefono fisso (decidiamo di prenderne uno e usare sempre quello).
// TODO: Verificare con matlab il range delle frequenze MIN-MAX con le nuove registrazioni, con quelle dall'ace erano 90-1100 hz.
// TODO: Costruire un modello svm con le feature di tutti i parlatori filtrate MIN-MAX.
// TODO: Scartare i frame in cui l'energia presente all'interno delle frequenze MIN-MAX sia inferiore ad una certa soglia (= silenzio dei parlatori principali).
// TODO: Scartare i frame in cui l'energia percentuale energia_interna / energia_esterna sia inferiore ad una certa soglia (= frame rumoroso).
// TODO: I frame rimanenti vanno filtrati MIN-MAX, prima di estrarre le feature e mandarle al predict.

// Se tutto questo non funziona possiamo arrenderci. Credo.

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class FeatureExtractor extends AsyncTask <String, Void, Boolean> {

    public final static int MFCC_COUNT = 13;
    public final static String TAG = "FeatureExtractor";

    private static ProgressDialog cProgressRecorder;
    private static Context cContext;

    public static double[][] MFCC = null;
    public static double[][] DeltaDelta = null;

    public static int frameExtracted = 0;

    protected void onPreExecute()
    {
        cContext = MainActivity.context;

        cProgressRecorder = new ProgressDialog(cContext);
        cProgressRecorder.setProgressNumberFormat(null);
        cProgressRecorder.setMax(100);

        cProgressRecorder = ProgressDialog.show(cContext, "Removing invalid frames...", "Energy based removal");
    }

    @Override
    protected Boolean doInBackground(String... params)
    {

        try
        {
            // params[0] = name of the audio file
            // TODO rimuovi la seguente linea di codice (era per test)
            //params[0] = MainActivity.PATH + "/" + "aceDavide0.wav";
            Timer t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    publishProgress();
                }
            }, 50, 50);

            MFCC = extractMFCC(params[0]);
            DeltaDelta = DD.computeDD(MFCC, 2); // 2 is the precision

            // If recognition mode is on:
            if (!MainActivity.isTraining)
            {
                // Delete wav file
                File file = new File(params[0]);
                file.delete();
                // Send SVM intent so that SVM recognition may start
                Intent intent = new Intent("it.unige.diten.dsp.speakerrecognition.SVM_EXTRACT");
                cContext.sendBroadcast(intent);
            }
            else
            // if training mode is on:
            {
                /**
                 * Save to file
                 * save with the same filename of the .wav with a different extension:
                 * ff = feature file
                 */
                String outputFileName = params[0].replace(MainActivity.AUDIO_EXT, MainActivity.FEATURE_EXT);
                writeFeatureFile(outputFileName, MFCC, DeltaDelta);
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
            return(Boolean.FALSE);
        }

        return(Boolean.TRUE);
    }

    @Override
    protected void onProgressUpdate(Void... out)
    {
        if(frameExtracted > 0)
        {
            int frameCount = Framer.getFrames().length;
            //int perc = (int)Math.floor(((float)frameExtracted / (float)frameCount * 100.0));
            cProgressRecorder.setTitle("Extracting features...");
            cProgressRecorder.setMessage(frameExtracted + "/" + frameCount);
        }
    }

    @Override
    protected void onPostExecute(Boolean cv)
    {
        cProgressRecorder.dismiss();
    }


    public static void writeFeatureFile(String fileName, double[][] MFCC, double[][] DeltaDelta)
    {
        // Stampo su file testuale i vettori
        try
        {
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
                    line += (k + 1) + ":" + Double.toString(MFCC[f][k]) + "\t";


                for (int k = 0; k < DD.DD_COUNT; k++)
                    line += (k + MFCC_COUNT + 1) + ":" + Double.toString(DeltaDelta[f][k]) + "\t";

                line = line.substring(0, line.length() - 2) + "\r\n";

                writer.write(line);
            }
            writer.close();
        }
        catch (IOException e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    public static double [][] extractMFCC(String audioFilename) throws java.lang.Exception
    {
        frameExtracted = 0;

        Framer.readFromFile(audioFilename);
        final Frame[] frames = Framer.getFrames();


        double[][] mfcc = new double[frames.length][]; // No need to create rows (FEThread already does it)


        int numCores = MainActivity.numCores;


        Thread[] threads = new Thread[numCores];
        for (int C = 0; C < numCores; C++)
        {
            threads[C] = new Thread(new FEThread(C, numCores, frames, mfcc));
            threads[C].start();
        }
        for (int C = 0; C < numCores; C++)
            threads[C].join();

        return mfcc;
    }


}
