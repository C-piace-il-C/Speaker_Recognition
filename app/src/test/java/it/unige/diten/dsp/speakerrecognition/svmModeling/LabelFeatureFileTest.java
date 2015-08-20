package it.unige.diten.dsp.speakerrecognition.svmModeling;

import org.junit.Before;
import org.junit.Test;

public class LabelFeatureFileTest {

    // Questi file devono avere un identificativo, ovvero nel nome del file ci deve essere il nome del parlatore
    // Attenzione che di default sono Andrea, Davide, Emanuele
    private String[] files = {"C:/Tests/SR/[a]Andrea.ff",
            "C:/Tests/SR/[a]Davide.ff",
            "C:/Tests/SR/[a]Emanuele.ff"};
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testLabel() throws Exception {
        String mergedFile = LabelFeatureFile.label(files);
        ModelFromFile modelFromFile = new ModelFromFile();
        modelFromFile.doInBackground(mergedFile, "Cross");

    }
}