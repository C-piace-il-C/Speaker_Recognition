package it.unige.diten.dsp.speakerrecognition.svmModeling;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LabelFeatureFileTest {

    private String files = "/home/doddo/Tests/modeling/AndreaDavideEmanuele.scaled";
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testLabel() throws Exception {
        ModelFromFile modelFromFile = new ModelFromFile();
        modelFromFile.doInBackground(files, "Cross");
    }
}