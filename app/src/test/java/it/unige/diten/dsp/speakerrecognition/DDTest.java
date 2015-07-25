package it.unige.diten.dsp.speakerrecognition;

import junit.framework.TestCase;

/**
 *
 */
public class DDTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }
    static double[][] result1,result2;
    public void testComputeDD1() throws Exception
    {
        // [frame][MFCC]
        // MFCC_k(f)/df
        double[][] input = new double[30][];
        for(int F = 0; F < 30; F++)
        {
            input[F] = new double[2];
            input[F][0] = 0.0;
            input[F][1] = Math.exp((double)F);
        }
        result1 = DD.computeDD_1(input, 2);
    }

    public void testComputeDD0() throws Exception
    {
        // [frame][MFCC]
        // MFCC_k(f)/df
        double[][] input = new double[30][];
        for(int F = 0; F < 30; F++)
        {
            input[F] = new double[2];
            input[F][0] = 0.0;
            input[F][1] = Math.exp((double)F);
        }
        result2 = DD.computeDD(input, 2);
    }
}