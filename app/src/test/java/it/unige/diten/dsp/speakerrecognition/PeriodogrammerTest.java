
package it.unige.diten.dsp.speakerrecognition;

import junit.framework.Assert;
import junit.framework.TestCase;


public class PeriodogrammerTest extends TestCase {
    static Frame f;
    public void setUp() throws Exception {
        super.setUp();

    }
    public boolean areEqual(double a, double b)
    {
        double abs = a-b < 0 ? (b-a) : (a-b);
        return(abs <= 0.00000000000001);
    }
    public void testComputePeriodogramSin() throws Exception
    {
        f = new Frame();
        f.data = new double[10];
        for(int C=0;C<10;C++)
            f.data[C] = Math.sin((double)C);

        double[] periodogram = Periodogrammer.computePeriodogram(f);
        double[] expectation = new double[10];
        int k = 0;

        expectation[k++] = 0.045896195961077;
        expectation[k++] = 0.370815577298035;
        expectation[k++] = 0.470100665735951;
        expectation[k++] = 0.033869120642130;
        expectation[k++] = 0.000069378645336;
        expectation[k++] = 0.000026984353050;
        expectation[k++] = 0.000069378645336;
        expectation[k++] = 0.033869120642130;
        expectation[k++] = 0.470100665735951;
        expectation[k] = 0.370815577298035;

        for(int C = 0;C<10;C++)
            Assert.assertTrue(areEqual(expectation[C], periodogram[C]));
    }
    public void testComputePeriodogramRamp() throws Exception
    {
        f = new Frame();
        f.data = new double[10];
        for(int C=0;C<10;C++)
            f.data[C] = (double)C;

        double[] periodogram = Periodogrammer.computePeriodogram(f);
        double[] expectation = new double[10];
        int k = 0;

        // Values obtained with MATLAB
        expectation[k++] = 49.417289999999987;
        expectation[k++] = 14.799697001687317;
        expectation[k++] = 0.193647653953483;
        expectation[k++] = 0.010550241063387;
        expectation[k++] = 0.012868259457088;
        expectation[k++] = 0.013655308884158;
        expectation[k++] = 0.012868259457088;
        expectation[k++] = 0.010550241063387;
        expectation[k++] = 0.193647653953483;
        expectation[k] = 14.799697001687317;

        for(int C = 0;C<10;C++)
            Assert.assertTrue(areEqual(expectation[C], periodogram[C]));
    }
}