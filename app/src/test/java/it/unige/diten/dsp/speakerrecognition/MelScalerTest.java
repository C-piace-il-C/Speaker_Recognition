// TODO: Testa sto melscaler con matlab
package it.unige.diten.dsp.speakerrecognition;

import junit.framework.Assert;
import junit.framework.TestCase;


public class MelScalerTest extends TestCase
{

    public void testExtractMelEnergiesOfConstant() throws Exception
    {
        double[] constantIn = new double[Framer.SAMPLES_IN_FRAME];
        for(int C = 0; C < Framer.SAMPLES_IN_FRAME; C++)
            constantIn[C] = 1.0;
        double[] constantOut = MelScaler.extractMelEnergies(constantIn);

        Assert.assertEquals(26, constantOut.length);
        System.out.println(constantOut);
    }
}