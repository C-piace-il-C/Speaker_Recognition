package it.unige.diten.dsp.speakerrecognition.svmModeling;

import org.junit.Before;
import org.junit.Test;

import it.unige.diten.dsp.speakerrecognition.SVMTraining.ModelFromFile;

public class LabelFeatureFileTest {

    // Questi file devono avere un identificativo, ovvero nel nome del file ci deve essere il nome del parlatore
    // Attenzione che di default sono Andrea, Davide, Emanuele
    private String[] files = {"/home/doddo/Tests/modeling2/[qualcosa]aceAndrea2.ff",
            "/home/doddo/Tests/modeling2/[qualcosa]aceDavide2.ff",
            "/home/doddo/Tests/modeling2/[qualcosa]aceEmanuele2.ff"};
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testLabel() throws Exception {
        String mergedFile = LabelFeatureFile.label(files);
        String scaledFile = ScaleFeatureFile.Scale(mergedFile);
        ModelFromFile modelFromFile = new ModelFromFile();
        modelFromFile.doInBackground(scaledFile, "Cross");

    }
}