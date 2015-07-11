package it.unige.diten.dsp.speakerrecognition;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RecognitionReceiver extends BroadcastReceiver {

    public static int result;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        MainActivity.updateRecognitionResults(result);
    }
}