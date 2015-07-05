package it.unige.diten.dsp.speakerrecognition;

import android.util.Log;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.Console;

public class DCTTest extends TestCase {

    static double[] values = {8, 8, 8, 8, 8, 8, 8, 8};
    static double[] results = new double[values.length];

    public boolean areEqual(double a, double b)
    {
        double abs = a-b < 0 ? (b-a) : (a-b);
        return(abs <= 0.1);
    }
    public void setUp() throws Exception {
        super.setUp();

    }

    public void testComputeDCTRamp() throws Exception
    {
        double[] input = new double[10];
        for( int C = 0; C < 10; C++ )
            input[C] = C;

        double[] output = DCT.computeDCT(input,10);

        double[] expected = new double[10];
        int k = 0;
        expected[k++] = 14.230249470757705;
        expected[k++] = -9.024851126140829;
        expected[k++] = 0;
        expected[k++] = -0.966656902772730;
        expected[k++] = 0;
        expected[k++] = -0.316227766016838;
        expected[k++] = 0;
        expected[k++] = -0.127870392685790;
        expected[k++] = 0;
        expected[k] = -0.035857300383897;

        for(int C =0; C < 10; C++)
            Assert.assertTrue(areEqual(expected[C],output[C]));
    }
}