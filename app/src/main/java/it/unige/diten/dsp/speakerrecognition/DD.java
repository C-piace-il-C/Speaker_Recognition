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
    public static final int DD_COUNT = FeatureExtractor.MFCC_COUNT;
    // Buona la prima.
    public static double[][] computeDD_0(double[][] src, int M)
    {
        // Per chiarezza del codice consiglio di rimpiazzare src.length
        // e src[0].length (roba asssolutamente criptica) con FRAME_COUNT e MFCC_COUNT
        // visto che li abbiamo...
        double ret[][] = new double[src.length][src[0].length];

        int numCores = MainActivity.numCores;
        Runnable[] runnables    = new DDThread[numCores];
        Thread[] threads        = new Thread[numCores];

        for(int C = 0; C < numCores; C++)
        {
            runnables[C] = new DDThread(C,numCores,ret,src,M);
            threads[C] = new Thread(runnables[C]);
            threads[C].start();
        }

        try {
            for (int C = 0; C < numCores; C++)
                threads[C].join();
        }
        catch(Exception e)
        {

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
