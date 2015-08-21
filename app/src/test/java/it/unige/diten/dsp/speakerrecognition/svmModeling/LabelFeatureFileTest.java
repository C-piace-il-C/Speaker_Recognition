package it.unige.diten.dsp.speakerrecognition.svmModeling;

import org.junit.Before;
import org.junit.Test;

import it.unige.diten.dsp.speakerrecognition.DD;
import it.unige.diten.dsp.speakerrecognition.FeatureExtractor;

public class LabelFeatureFileTest {

    private String[] files = {
            "/home/doddo/Generic_Workspace/Matlab/ace/[a]aceAndrea2.wav",
            "/home/doddo/Generic_Workspace/Matlab/ace/[a]aceDavide2.wav"};
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testLabel() throws Exception {
        String[] features = new String[files.length];
        int i = 0;
        for(String file : files)
        {
            double[][] MFCC, DDelta;
            MFCC = FeatureExtractor.extractMFCC(file);
            DDelta   = DD.computeDD(MFCC, 2);
            features[i] = file.replace(".wav", ".ff");
            FeatureExtractor.writeFeatureFile(features[i], MFCC, DDelta);
            i++;
        }

        String mergedFile = LabelFeatureFile.label(features);
        String scaledFile = ScaleFeatureFile.Scale(mergedFile);
        ModelFromFile modelFromFile = new ModelFromFile();
        modelFromFile.doInBackground(scaledFile, "Cross");

    }
}