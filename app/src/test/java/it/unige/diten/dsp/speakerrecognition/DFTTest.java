package it.unige.diten.dsp.speakerrecognition;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.Timestamp;

/**
 *
 */
public class DFTTest extends TestCase
{


    public boolean areEqual(double a, double b)
    {
        double abs = a-b < 0 ? (b-a) : (a-b);
        return(abs <= 0.00000001);
    }
    public void setUp() throws Exception {
        super.setUp();

    }

    public void testDFTofSin1() throws Exception
    {
        int seqLength = 256;
/*
        double[] input = new double[10];
        for(int i=0;i<input.length;i++)
            input[i] = Math.sin((double)i);
*/
        Complex[] input = new Complex[seqLength];
        for(int i = 0; i < input.length; i++)
        {
            input[i] = new Complex();

            input[i].Re = Math.sin((double)i);
            input[i].Im = 0;
        }
        Complex[] res = new Complex[seqLength];
        for(int i=0;i<res.length;i++)
            res[i] = new Complex();

        double[] input_DFT = new double[seqLength];
        for(int i=0;i<input.length;i++)
            input_DFT[i] = Math.sin((double)i);

        Complex[] res_DFT = new Complex[seqLength];
        for(int i=0;i<res.length;i++)
            res_DFT[i] = new Complex();

        long startDFT = System.nanoTime();
        DFT.computeDFT(input_DFT, res_DFT);
        long endDFT = System.nanoTime();

        long startFFT = System.nanoTime();
        res = FFT.fft(input);
        long endFFT = System.nanoTime();

        try
        {
            File file = new File("/home/doddo/Desktop/res.txt");
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.append("FFT: " + (endFFT - startFFT) + "\n");
            fileWriter.append("DFT: " + (endDFT - startDFT) + "\n");
            fileWriter.close();
        }
        catch(Exception ew)
        {
            ew.printStackTrace();
        }

        Complex[] expectedRes = new Complex[10];
        int k = 0;
        // Values obtained with MATLAB
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
/*
        for(int j=0;j<10;j++)
        {
            Assert.assertTrue(areEqual(expectedRes[j].Re, res[j].Re));
            Assert.assertTrue(areEqual(expectedRes[j].Im, res[j].Im));
        }
*/
    }
/*
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

        // Values obtained with MATLAB
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

    }*/
}