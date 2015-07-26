package it.unige.diten.dsp.speakerrecognition;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import java.io.File;


public class Rec extends AsyncTask<String, Void, Boolean>
{
    private ProgressDialog  cProgressRecorder = null;
    private Context         cContext;
    private AudioRecord     cRecorder = null;
    int                     cRegistrationLenghtInSeconds;
    int                     cFS;
    int                     cNumberOfSamples;

    short[]                 cAudioData = null;

    public Rec(Context context, int registrationLength, int sampleFrequency)
    {
        this.cContext                       = context;
        this.cRegistrationLenghtInSeconds   = registrationLength;
        this.cFS                            = sampleFrequency;
        this.cNumberOfSamples               = this.cFS * this.cRegistrationLenghtInSeconds;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        cProgressRecorder = new ProgressDialog(cContext);
        cProgressRecorder.setIndeterminate(true);
        cProgressRecorder = ProgressDialog.show(cContext, "Recording Audio...", "Speak Now.");

        cRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, cFS, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, cNumberOfSamples * 2);
    }

    @Override
    protected Boolean doInBackground(String... params)
    {
        String storDir = params[0];
        String fileName = params[1];

        cAudioData = new short[cNumberOfSamples];

        try
        {
            cRecorder.startRecording();
            cRecorder.read(cAudioData, 0, cNumberOfSamples);
        }
        catch (IllegalStateException ise)
        {
            ise.printStackTrace();
            return false;
        }

        File file = new File(storDir);
        if (!file.exists())
            if (!file.mkdir()) {
                String TAG = "Audio Recorder";
                Log.e(TAG,"An error occurred while trying to create the directory.");
                return false;
            }

        WAVCreator wav = new WAVCreator(storDir + "/" + fileName, cAudioData, 8000, 1);
        wav.write();

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
        //cProgressRecorder = null;

        // Send intent.
        Intent intent = new Intent("it.unige.diten.dsp.speakerrecognition.FEATURE_EXTRACT");
        cContext.sendBroadcast(intent);
    }


}