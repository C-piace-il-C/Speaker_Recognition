package it.unige.diten.dsp.speakerrecognition;

/**
 * Delta-Deltas.
 */
public abstract class DD
{
    public static final int DD_COUNT = FeatureExtractor.MFCC_COUNT;

    /**
     * Computes the DeltaDelta matrix of the matrix src[i][f] (i: frame index, f: feature index).
     * @param src   Matrix from which are extracted the Delta-Deltas.
     * @param M     The precision of the DD calculation.
     * @return      The DeltaDelta matrix, with the same size of src.
     */
    public static double[][] computeDD(double[][] src, int M)
    {
        double DD_Matrix[][] = new double[src.length][src[0].length];

        try
        {
            Thread[] threads  = new Thread[MainActivity.numCores];

            // Start workers.
            for(int i = 0; i < MainActivity.numCores; i++)
            {
                threads[i] = new Thread(new DDThread(i, MainActivity.numCores, DD_Matrix, src, M));
                threads[i].start();
            }

            // Wait workers to finish.
            for (Thread t : threads) { t.join(); }
        }
        catch (Exception e) { e.printStackTrace(); }

        return DD_Matrix;
    }

}
