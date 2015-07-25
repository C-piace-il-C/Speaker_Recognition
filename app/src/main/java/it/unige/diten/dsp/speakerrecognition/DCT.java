// Completely tested.

package it.unige.diten.dsp.speakerrecognition;

/**
 * Type-II DCT.
 */
public abstract class DCT
{
    /**
     * @brief       Computes the type-II DCT on a real sequence src.
     *              The result is a new real sequence of the same length as src.
     * @param src   The source sequence.
     * @param len   The number of samples of the DCT to compute.
     *              This will also be the length of the returned sequence.
     *              Default value is src.length.
     * @return      double array containing the DCT.
     */
    public static double[] computeDCT (double[] src, int len)
    {
        final double N = src.length;
        double[] DCT = new double[len];

        double adj = Math.sqrt(2.0 / N);

        // Compute only the first len values
        for (int k = 0; k < len; k++)
        {
            DCT[k] = .0;
            for (int n = 0; n < N; n++)
            {
                DCT[k] += src[n] * Math.cos(Math.PI / N * (n + 0.5) * k);
            }
            DCT[k] *= adj;
        }

        DCT[0] /= Math.sqrt(2.0);

        return (DCT);
    }

    public static double[] computeDCT(double[] src)
    {
        return computeDCT(src,src.length);
    }
}
