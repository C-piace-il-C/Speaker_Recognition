package it.unige.diten.dsp.speakerrecognition;

/**
 * Delta-Deltas.
 */
public abstract class DD
{
    public static double[][] computeDD (double[][] src, int M)
    {
        double ret[][] = new double[src.length][src[0].length];

        // Sum of first N squares: (N(N + 1)(2N + 1)) / 6.
        double adj = Math.pow(2 * (M * (M + 1) * (2 * M + 1)) / 6.0, 2);

        // f: frame index
        for (int f = 0; f < src.length; f++)
        {
            // k: CC index
            for (int k = 0; k < src[0].length; k++)
            {
                ret[f][k] = .0;

                for (int m = -M; m <= M; m++)
                {
                    for (int n = -M; n <= M; n++)
                    {
                        if ((f + m + n >= 0) && (f + m + n < src.length))
                            ret[f][k] += m * n *  src[f + m + n][k];
                    }
                }

                ret[f][k] /= adj;
            }
        }

        return ret;
    }
}
