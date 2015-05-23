package it.unige.diten.dsp.speakerrecognition;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import it.unige.diten.dsp.speakerrecognition.WavIO.WavIO;
/**
 * RecAndRell
 * Questa AsyncTask si occupa della registrazione. Quando viene avviata, prende come input una
 * stringa (path) con la directory dove salvare la registrazione e una seconda (behaviour) che
 * specifica ilcomportamaento successivo.
 * Behaviour = "0": TRAIN
 *      Il processo di registrazione e' stato avviato con l'intenzione di allenare la SVM.
 *      Una volta registrato e salvato il file WAV, si procede con l'estrazione di feature salvando
 *      un file in una directory dedicata a parte (che conterra' esclusivamente i file di feature
 *      per allenare la SVM). Il file verra' inviato alla SVM in un secondo momento dall'utente
 *      attraverso l'interfaccia grafica del programma (magari attraverso un pulsante "invia"
 *      che spedisce alla SVM tutto il contenuto della cartella).
 * Behaviour = "1": RECOGNIZE
 *      Il processo di registrazione e' stato avviato con l'intenzione di riconoscere il parlatore.
 *      Si procede con l'estrazione di feature, ma in questo caso il file prodotto viene inviato
 *      automaticamente alla SVM in modalita' di il riconoscimento.
 */
public class RecAndRell extends AsyncTask<String, Void, Integer> {

    final int waitPreRecording = 2000;
    private final String TAG = "Recorder Audio";
    int cRegistrationLenghtInSeconds;
    int cFS;
    int cNumberOfSamples;
    short[] cAudioData = null;
    private ProgressDialog cProgressRecorder = null, cProgressFeatures = null;
    private Context cContext;
    private AudioRecord cRecorder = null;


    private String fileName = "";

    public RecAndRell(Context context, int registrationLenght, int sampleFrequency, short[] audioData) {
        this.cContext = context;
        this.cRegistrationLenghtInSeconds = registrationLenght;
        this.cFS = sampleFrequency;

        this.cNumberOfSamples = this.cFS * this.cRegistrationLenghtInSeconds;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        cProgressRecorder = new ProgressDialog(cContext);
        cProgressRecorder.setIndeterminate(true);
        cProgressRecorder = ProgressDialog.show(cContext, "Recording Audio...", "Speak Now.");

        cProgressFeatures = new ProgressDialog(cContext);
        cProgressFeatures.setIndeterminate(true);

        cRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, cFS, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, cNumberOfSamples * 2);
    }

    @Override
    protected Integer doInBackground(String... params) {
        String _path = params[0];
        String _fileName = params[1];

        /*
         *   _behaviour:
         *     "0" - Train
         *     "1" - Recognize
         */

        String _behaviour = params[2];

        Log.i(TAG, "REC: Start!");

        cAudioData = new short[cNumberOfSamples];

        cRecorder.startRecording();
        cRecorder.read(cAudioData, 0, cNumberOfSamples);

        String storDir = Environment.getExternalStorageDirectory() + "/" + _path;

        fileName = storDir + "/" + _fileName;

        File file = new File(storDir);
        if (!file.exists())
            if (!file.mkdir())
                showError("Error occurred while trying to create the directory.");

        byte[] dataByte = new byte[2 * cNumberOfSamples];

        for (int i = 0; i < cNumberOfSamples; i++) {
            dataByte[2 * i]     =   (byte) (cAudioData[i]           & 0x00FF);
            dataByte[2 * i + 1] =   (byte) ((cAudioData[i] >> 8)    & 0x00FF);
        }

        WavIO writeWav = new WavIO(fileName, 16, 1, 1, 8000, 2, 16, dataByte);
        writeWav.save();

        /*   _behaviour:
         *     "0" - Train
         *     "1" - Recognize  */
        return(Integer.parseInt(_behaviour));
    }

    @Override
    protected void onPostExecute(Integer behaviour) {
        super.onPostExecute(behaviour);

        cRecorder.stop();
        cRecorder.release();

        Log.i("OnClick", "Rec: Stop!");

        cProgressRecorder.dismiss();
        cProgressRecorder = null;

        // After recording audio to fileName, extract features
        FeatureExtractor fe = new FeatureExtractor();
        fe.execute(fileName);

        if(behaviour == 0) {
            // train
        } else {
            // recognize
        }
    }

    private void showError(String string) {
        Toast.makeText(cContext, string, Toast.LENGTH_LONG).show();
        Log.e("ERRORE:", string);
    }


}
