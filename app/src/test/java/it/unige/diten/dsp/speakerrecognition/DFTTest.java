package it.unige.diten.dsp.speakerrecognition;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 *
 */
public class DFTTest extends TestCase {

    static double[] values = {0, 2, 4, 8, 16, 8, 4, 2, 0};

    public boolean areEqual(double a, double b)
    {
        double abs = a-b < 0 ? (b-a) : (a-b);
        return(abs <= 0.0001);
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
    public void testDFTofSin1() throws Exception
    {
        double[] input = new double[10];
        for(int i=0;i<input.length;i++)
            input[i] = Math.sin((double)i);

        Complex[] res = new Complex[10];
        for(int i=0;i<10;i++)
            res[i] = new Complex();

        DFT.computeDFT(input, res);

        Complex[] expectedRes = new Complex[10];
        int k = 0;
        expectedRes[k++] = new Complex(1.955209482107380,0.000000000000000);
        expectedRes[k++] = new Complex(3.151505791271589,0.594994616210640);
        expectedRes[k++] = new Complex(-3.073479308812598,-1.118520711871261);
        expectedRes[k++] = new Complex(-0.639028043502262,-0.304593821408143);
        expectedRes[k++] = new Complex(-0.301436118898938,-0.118492185600151);
        expectedRes[k++] = new Complex(-0.230334122222961,0.000000000000000);
        expectedRes[k++] = new Complex(-0.301436118898938,0.118492185600151);
        expectedRes[k++] = new Complex(-0.639028043502262,0.304593821408143);
        expectedRes[k++] = new Complex(-3.073479308812598,1.118520711871261);
        expectedRes[k] = new Complex(3.151505791271589,-0.594994616210640);

        for(int j=0;j<10;j++)
        {
            Assert.assertTrue(areEqual(expectedRes[j].Re, res[j].Re));
            Assert.assertTrue(areEqual(expectedRes[j].Im, res[j].Im));
        }

    }

    public void testDFTofRamp()
    {
        double[] input = new double[10];
        for(int i=0;i<input.length;i++)
            input[i] = i;

        Complex[] res = new Complex[10];
        for(int i=0;i<10;i++)
            res[i] = new Complex();

        DFT.computeDFT(input, res);

        Complex[] expectedRes = new Complex[10];
        int k = 0;
        expectedRes[k++] = new Complex(45.000000000000000,0.000000000000000);
        expectedRes[k++] = new Complex(-5.000000000000000,15.388417685876266);
        expectedRes[k++] = new Complex(-5.000000000000000,6.881909602355867);
        expectedRes[k++] = new Complex(-5.000000000000000,3.632712640026803);
        expectedRes[k++] = new Complex(-5.000000000000000,1.624598481164532);
        expectedRes[k++] = new Complex(-5.000000000000000,0.000000000000000);
        expectedRes[k++] = new Complex(-5.000000000000000,-1.624598481164532);
        expectedRes[k++] = new Complex(-5.000000000000000,-3.632712640026803);
        expectedRes[k++] = new Complex(-5.000000000000000,-6.881909602355867);
        expectedRes[k] = new Complex(-5.000000000000000,-15.388417685876266);


        for(int j=0;j<10;j++)
        {
            Assert.assertTrue(areEqual(expectedRes[j].Re, res[j].Re));
            Assert.assertTrue(areEqual(expectedRes[j].Im, res[j].Im));
        }

    }
}