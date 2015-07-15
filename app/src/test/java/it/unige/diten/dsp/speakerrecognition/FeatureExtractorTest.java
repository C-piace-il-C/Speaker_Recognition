package it.unige.diten.dsp.speakerrecognition;

import junit.framework.TestCase;

/**
 * Created by doddo on 7/15/15.
 */
public class FeatureExtractorTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void testExtractMFCC() throws Exception {

        double[][] result = FeatureExtractor.extractMFCC("/home/doddo/Tests/test.wav");

        int a = 2;
    }
}