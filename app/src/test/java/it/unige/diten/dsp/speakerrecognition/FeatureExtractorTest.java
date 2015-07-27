package it.unige.diten.dsp.speakerrecognition;

import junit.framework.TestCase;

public class FeatureExtractorTest extends TestCase
{

    public void setUp() throws Exception {
        super.setUp();

    }

    public void testExtractFeatures() throws Exception
    {
        String path = "G:\\Lavori\\Software\\Speaker_Recognition";
        double[][] MFCC = FeatureExtractor.extractMFCC(path + "\\Matlab\\Test Units\\aceAndrea0frame0.wav");
        //double[][] DeDe = DD.computeDD(MFCC,2);

        TextWriter.setFilename(path + "\\Matlab\\Test Units\\appfeatAceAndrea0frame0.txt");
        for(int C = 0; C < MFCC[0].length; C++)
            TextWriter.appendText(String.valueOf(MFCC[0][C]));
    }
}