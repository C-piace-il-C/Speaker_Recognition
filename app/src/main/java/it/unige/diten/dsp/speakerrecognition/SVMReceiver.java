package it.unige.diten.dsp.speakerrecognition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SVMReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO start SVM shit
        MainActivity.updateRecognitionResults(
                MySVM.RecognizeSpeaker()
        );
    }
}
