package it.unige.diten.dsp.speakerrecognition;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class Rec extends AsyncTask<String, Void, Boolean> {

    final int waitPreRecording = 2000;
    private final String TAG = "Recorder Audio";
    int cRegistrationLenghtInSeconds;
    int cFS;
    int cNumberOfSamples;
    short[] cAudioData = null;
    private ProgressDialog cProgressRecorder = null, cProgressFeatures = null;
    private Context cContext;
    private AudioRecord cRecorder = null;

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

        cProgressFeatures = new ProgressDialog(cContext);
        cProgressFeatures.setIndeterminate(true);

        cRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, cFS, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, cNumberOfSamples * 2);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String _path = params[0];
        String _fileName = params[1];

        Log.i(TAG, "REC: Start!");

        cAudioData = new short[cNumberOfSamples];

        try {
            cRecorder.startRecording();
            cRecorder.read(cAudioData, 0, cNumberOfSamples);
        } catch (IllegalStateException ise) {
            showError(ise.getMessage());
            return false;
        }

        String storDir = Environment.getExternalStorageDirectory() + "/" + _path;
        File file = new File(storDir);
        if (!file.exists())
            if (!file.mkdir())
                showError("Impossibile creare la cartella.");

        WAVCreator wavCreator = new WAVCreator(storDir + "/" + _fileName, cAudioData, 8000, 1);
        wavCreator.write();

        return true;
    }

    @Override
    protected void onPostExecute(Boolean cv)
    {
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

    private void showError(String string)
    {
        Toast.makeText(cContext, string, Toast.LENGTH_LONG).show();
        Log.e("ERRORE:", string);
    }

}