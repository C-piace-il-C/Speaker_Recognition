package it.unige.diten.dsp.speakerrecognition;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RecognitionReceiver extends BroadcastReceiver {

    public static int result;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // MySVM_Async just performed the recognition, set result and sent this intent
        MainActivity.updateRecognitionResults(result);
    }
}