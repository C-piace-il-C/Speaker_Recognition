package it.unige.diten.dsp.speakerrecognition;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import it.unige.diten.dsp.speakerrecognition.WavIO.WavIO;

public class Rec extends AsyncTask<String, Void, Boolean> {

    private ProgressDialog cProgressRecorder = null;
    private Context cContext;
    private AudioRecord cRecorder = null;
    int cRegistrationLenghtInSeconds;
    int cFS;
    int cNumberOfSamples;
    // TODO I do not know what to do with my life
    final int waitPreRecording = 2000;

    private final String TAG = "Recorder Audio";
    short[] cAudioData = null;

    public Rec(Context context, int registrationLenght, int sampleFrequency) {
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

        cRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, cFS, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, cNumberOfSamples * 2);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String storDir = params[0];
        String fileName = params[1];

        Log.i(TAG, "REC: Start!");

        cAudioData = new short[cNumberOfSamples];

        try {
            cRecorder.startRecording();
            cRecorder.read(cAudioData, 0, cNumberOfSamples);
        } catch (IllegalStateException ise) {
            showError(ise.getMessage());
            return false;
        }

        File file = new File(storDir);
        if (!file.exists())
            if (!file.mkdir())
                showError("Impossibile creare la cartella.");

        byte[] dataByte = new byte[2 * cNumberOfSamples];

        for (int i = 0; i < cNumberOfSamples; i++) {
            dataByte[2 * i] = (byte) (cAudioData[i] & 0x00FF);
            dataByte[2 * i + 1] = (byte) ((cAudioData[i] >> 8) & 0x00FF);
        }

        WavIO writeWav = new WavIO(storDir + "/" + fileName, 16, 1, 1, 8000, 2, 16, dataByte);
        writeWav.save();

        return true;
    }

    @Override
    protected void onPostExecute(Boolean cv) {
        super.onPostExecute(cv);

        cRecorder.stop();
        cRecorder.release();

        Log.i("OnClick", "Rec: Stop!");

        cProgressRecorder.dismiss();
        cProgressRecorder = null;

        // Send intent.
        Intent intent = new Intent("it.unige.diten.dsp.speakerrecognition.FEATURE_EXTRACT");
        cContext.sendBroadcast(intent);
    }

    private void showError(String string) {
        Toast.makeText(cContext, string, Toast.LENGTH_LONG).show();
        Log.e("ERRORE:", string);
    }

}