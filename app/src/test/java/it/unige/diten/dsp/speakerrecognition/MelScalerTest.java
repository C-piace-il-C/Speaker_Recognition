package it.unige.diten.dsp.speakerrecognition;

import junit.framework.TestCase;

/**
 * Created by Emanuele on 06/07/2015.
 */
public class MelScalerTest extends TestCase
{
    static double[] constantIn  = null;
    static double[] constantOut = null;

    public void setUp() throws Exception
    {
        super.setUp();

        constantIn = new double[Framer.SAMPLES_IN_FRAME];
        for (int i = 0; i < constantIn.length; i++)
        {
            constantIn[i] = 1.0;
        }
    }

    public void testExtractMelEnergies() throws Exception
    {
        constantOut = MelScaler.extractMelEnergies(constantIn);

        System.out.println(constantOut);
    }
}