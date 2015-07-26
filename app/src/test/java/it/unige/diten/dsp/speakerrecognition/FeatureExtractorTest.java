// TODO: Perform feature extraction on MATLAB to get values to compare with

package it.unige.diten.dsp.speakerrecognition;

import junit.framework.TestCase;

public class FeatureExtractorTest extends TestCase
{

    public void setUp() throws Exception {
        super.setUp();

    }

    public void testExtractFeatures() throws Exception
    {
        double[][] MFCC = FeatureExtractor.extractMFCC("/home/doddo/Tests/test.wav");
        double[][] DeDe = DD.computeDD(MFCC,2);
    }
}