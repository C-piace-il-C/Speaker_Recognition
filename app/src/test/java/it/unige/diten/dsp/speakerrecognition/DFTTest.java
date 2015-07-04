package it.unige.diten.dsp.speakerrecognition;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 *
 */
public class DFTTest extends TestCase {

    static double[] values = {0, 2, 4, 8, 16, 8, 4, 2, 0};
    static Complex[] results = new Complex[values.length];

    public void setUp() throws Exception {
        super.setUp();

    }

    public void testComputeDFT() throws Exception {
        for(int i = 0; i < results.length; i++) {
            results[i] = new Complex();
        }

        DFT.computeDFT(values, results);
        /*
        for(int i = 0; i < values.length; i++)
        {
            Assert.assertEquals(0., results[i].re);
            Assert.assertEquals(0., results[i].im);
        }*/
    }

    public void testComputeIDFT() throws Exception {
        DFT.computeIDFT(results, values);
        /*
        for(int i = 0; i < values.length; i++)
        {
            Assert.assertEquals(8., values[i]);
        }
        */
    }
}