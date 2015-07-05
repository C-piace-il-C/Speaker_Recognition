package it.unige.diten.dsp.speakerrecognition;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 *
 */
public class DFTTest extends TestCase {

    static double[] values = {0, 2, 4, 8, 16, 8, 4, 2, 0};
    static Complex[] results = new Complex[values.length];

    public Boolean areEqual(double a, double b)
    {
        double abs = (a-b < 0 ? b-a : a-b);
        return(abs < 0.0001);
    }
    public void setUp() throws Exception {
        super.setUp();

    }
/*
    public void testComputeDFT() throws Exception {
        for(int i = 0; i < results.length; i++) {
            results[i] = new Complex();
        }

        DFT.computeDFT(values, results);

        for(int i = 0; i < values.length; i++)
        {
            Assert.assertEquals(0., results[i].Re);
            Assert.assertEquals(0., results[i].Im);
        }
    }

    public void testComputeIDFT() throws Exception {
        DFT.computeIDFT(results, values);

        for(int i = 0; i < values.length; i++)
        {
            Assert.assertEquals(8., values[i]);
        }

    }

*/
    public void testDFTofSin1() throws Exception {
        double[] input = new double[10];
        for(int i=0;i<input.length;i++)
            input[i] = Math.sin((double)i);

        Complex[] res = new Complex[10];
        for(int i=0;i<10;i++)
            res[i] = new Complex();
        DFT.computeDFT(input, res);

        Complex[] expectedRes = new Complex[10];
    }
}