package it.unige.diten.dsp.speakerrecognition;

/**
 * DCT.
 * Type-II DCT.
 */
public abstract class DCT
{
    /**
     * Computes the type-II DCT on a real sequence src.
     * The result is a new real sequence of the same length as src.
     * @param src   The source sequence.
     * @param len   The number of samples of the DCT to compute.
     * @return      Double array containing the DCT.
     */
    public static double[] computeDCT (double[] src, int len)
    {
        double   N      = src.length;
        double   adj    = Math.sqrt(2.0 / N);
        double   stride = Math.PI / N;

        double[] DCT    = new double[len];

        // Compute only the first len values
        for (int i = 0; i < len; i++)
        {
            DCT[i] = .0;

            for (int n = 0; n < N; n++)
            {
                DCT[i] += src[n] * Math.cos(stride * (n + 0.5) * i);
            }

            // Scale coefficients.
            DCT[i] *= adj;
        }

        DCT[0] /= Math.sqrt(2.0);

        return (DCT);
    }

    /**
     * Computes the type-II DCT on a real sequence src.
     * The result is a new real sequence of the same length as src.
     * @param src   The source sequence.
     * @return      Double array containing the DCT.
     */
    public static double[] computeDCT(double[] src)
    {
        return computeDCT(src, src.length);
    }
}
