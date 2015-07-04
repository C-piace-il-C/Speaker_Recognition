package it.unige.diten.dsp.speakerrecognition;

import android.util.Log;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.Console;

/**
 * Created by doddo on 7/4/15.
 */
public class DCTTest extends TestCase {

    static double[] values = {8, 8, 8, 8, 8, 8, 8, 8};
    static double[] results = new double[values.length];

    public void setUp() throws Exception {
        super.setUp();

    }

    public void testComputeDCT() throws Exception {
        results = DCT.computeDCT(values, 8);
        for(int i = 1; i < results.length; i++) {
            String val = String.valueOf(results[i]);
            Log.v("VALUE:", val);
            Assert.assertEquals(results[i], 0.);
        }
    }
}