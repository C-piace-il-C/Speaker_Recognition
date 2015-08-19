package it.unige.diten.dsp.speakerrecognition;

import org.junit.Before;
import org.junit.Test;

import static  org.junit.Assert.*;


public class FCleanerTest {
    private Complex[] h;

    @Before
    public void setUp() throws Exception
    {
        WAVCreator creator = new WAVCreator("C:\\Tests\\audioSample.wav");
        creator.read();

        short[] samples = creator.getSamples();
        int len = samples.length;

        h = new Complex[len];
        int C;
        for(C = 0; C < len/6; C++)
            h[C] = new Complex(1.0,0.0);
        for(; C < len-len/6; C++)
            h[C] = new Complex(0.0,0.0);
        for(C = len-len/6; C < len; C++)
            h[C] = new Complex(1.0,0.0);

        Complex[] seqFt = new Complex[len];
        double[] cSamples = new double[len];
        for(C = 0; C < len; C++)
        {
            cSamples[C] = samples[C] + .0;//new Complex(samples[C]+.0, .0);
            seqFt[C] = new Complex();
        }

        // transform both sequences
        DFT.computeDFT(cSamples, seqFt);//FFT.fft(cSamples);
        //DFT.computeDFT(h, hFt);

        // perform filtering
        double[] result  = FCleaner.filterSeq(seqFt, h);
        short[]  sResult = new short[len];
        for(C = 0; C < len; C++ )
            sResult[C] = (short)result[C];

        WAVCreator due = new WAVCreator("C:\\Tests\\output.wav", sResult, 8000, 1);
        due.write();
    }

    @Test
    public void testFilter()
    {
        // tu devi ascoltare e dirmi se ti piace, porco.
        assertEquals(1,1); // per testare.
    }

}