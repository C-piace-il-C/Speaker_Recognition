// TESTATI GROSSOLANAMENTE (ponendo come input x, x^2, x^3, e^x e osservando qualitativamente il
// risultato) è uscito che la versione del paper (DD0) è corretta e meno onerosa computazionalmente
// mentre la DD1, tratta dalle dispense di GRATTAROLA, è totalmente sbagliata per x^2 e x^3
// (resta da vedere se ci sono bug nel'implementazione)

package it.unige.diten.dsp.speakerrecognition;

/**
 * Delta-Deltas.
 */
public abstract class DD
{
    // Buona la prima.
    public static double[][] computeDD_0(double[][] src, int M)
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
                        // Check bounds
                        if ((f + m + n >= 0) && (f + m + n < src.length))
                            ret[f][k] += m * n *  src[f + m + n][k];
                    }
                }

                ret[f][k] /= adj;
            }
        }

        return ret;
    }

    public static double[][] computeDD_1(double[][] src, int M)
    {
        double ret[][] = new double[src.length][src[0].length];

        // k: MFCC index
        for (int k = 0; k < src[0].length; k++) {
            // f: frame index
            for (int f = 0; f < src.length; f++) {
                ret[f][k] = .0;

                for (int m = -M; m <= M; m++) {
                    if(m==0)
                        continue;
                    double temp = .0;

                    for (int n = -M; n <= M; n++) {
                        if(n==0)
                            continue;
                        if ((f - m - n >= 0) && (f - m - n < src.length))
                            temp += Math.pow(-1, n) / (double) n * src[f - m - n][k];
                    }

                    ret[f][k] += Math.pow(-1, m) / (double) m * temp;
                }
            }
        }
        return ret;
    }
}
