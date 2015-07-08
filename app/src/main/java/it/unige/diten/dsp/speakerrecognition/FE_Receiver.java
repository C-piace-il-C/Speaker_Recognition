package it.unige.diten.dsp.speakerrecognition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

public class FE_Receiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Start Feature Extractor to create FEATURE file.
        FeatureExtractor featureExtractor = new FeatureExtractor();
        featureExtractor.execute(Environment.getExternalStorageDirectory() +
                        "/" + MainActivity.PATH + "/" + MainActivity.fileName);
    }


}
