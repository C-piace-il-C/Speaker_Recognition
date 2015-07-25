package it.unige.diten.dsp.speakerrecognition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class FEReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Rec just finished recording, it is time to extract features.
        // Start Feature Extractor to create the FEATURE file.
        FeatureExtractor featureExtractor = new FeatureExtractor();
        featureExtractor.execute(MainActivity.PATH + "/" + MainActivity.fileName);
    }
}
