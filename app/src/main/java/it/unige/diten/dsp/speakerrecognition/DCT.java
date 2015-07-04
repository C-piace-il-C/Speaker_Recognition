package it.unige.diten.dsp.speakerrecognition;

/**
 * Type-II DCT.
 */
public abstract class DCT
{
    public static double[] computeDCT (double[] src, int len)
    {
        final double precision = Math.pow(10, -6);
        final double N = src.length;
        double[] retV = new double[len];

        for (int k = 0; k < len; k++)
        {
            retV[k] = .0;

            for (int n = 0; n < N; n++)
            {
                retV[k] += src[n] * Math.cos((Math.PI / N) * (n + 0.5) * k);
            }
            // Scaling factor
            retV[k] *= (1/N);
            // Under a certain value we can consider it as if it were 0
            if(Math.abs(retV[k]) < precision) retV[k] = 0;
        }

        return (retV);
    }
}
