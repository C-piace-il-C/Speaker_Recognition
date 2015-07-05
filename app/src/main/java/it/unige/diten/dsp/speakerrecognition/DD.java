package it.unige.diten.dsp.speakerrecognition;

/**
 * Delta-Deltas.
 */
public abstract class DD
{
    public static double[][] computeDD (double[][] src, int M)
    {
        // Per chiarezza del codice consiglio di rimpiazzare src.length
        // e src[0].length (roba asssolutamente criptica) con FRAME_COUNT e MFCC_COUNT
        // visto che li abbiamo...
        double ret[][] = new double[src.length][src[0].length];

        // Sum of first N squares: (N(N + 1)(2N + 1)) / 6.
        double adj = Math.pow(2 * (M * (M + 1) * (2 * M + 1)) / 6.0, 2);

        // k: MFCC index
        for (int k = 0; k < src[0].length; k++)
        {
            // f: frame index
            for (int f = 0; f < src.length; f++)
            {
                ret[f][k] = .0;

                for (int m = -M; m <= M; m++)
                {
                    // meglio: 
                    // for(int n = min; n <= max; n++) invece di un if dentro ogni iterazione del for
                    // visto che f+m+n >= 0, e quindi n >= -f -m, allora min = Math.max(-f-m,-M)
                    // visto che f+m+n < src.length, e quindi n < src.length - m - f, allora 
                    //      max = Math.min(src.length - m - f, M)
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
