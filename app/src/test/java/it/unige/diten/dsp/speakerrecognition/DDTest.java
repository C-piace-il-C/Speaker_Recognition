package it.unige.diten.dsp.speakerrecognition;

import junit.framework.TestCase;

/**
 *
 */
public class DDTest extends TestCase {

    static double constant[][] = {{8, 8, 8, 8},{8, 8, 8, 8},{8, 8, 8, 8},{8, 8, 8, 8}};
    static double results[][] = new double[4][4];
    public void setUp() throws Exception {
        super.setUp();

    }

    public void testComputeDD() throws Exception {
        results = DD.computeDD(constant, 2);
    }
}