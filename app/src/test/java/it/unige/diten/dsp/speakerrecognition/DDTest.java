// TODO Implementa la DD in matlab e fai la verifica qui
package it.unige.diten.dsp.speakerrecognition;

import junit.framework.TestCase;

public class DDTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }
    static double[][] result1,result2;


    public void testComputeDD() throws Exception
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