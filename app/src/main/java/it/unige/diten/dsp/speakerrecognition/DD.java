package it.unige.diten.dsp.speakerrecognition;

/**
 * Delta-Deltas.
 */
public abstract class DD
{
    public static final int DD_COUNT = FeatureExtractor.MFCC_COUNT;
    /**
     * @brief       Computes the DeltaDelta matrix of the matrix src[f][fe], where
     *              f is the frame index and fe is the feature index.
     * @param M     The precision of the DD.
     * @return      The DeltaDelta matrix, with the same
     */
    public static double[][] computeDD(double[][] src, int M)
    {
        double ret[][] = new double[src.length][src[0].length];

        try
        {
            int numCores = MainActivity.numCores;

            Thread[] threads = new Thread[numCores];
            for(int C = 0; C < numCores; C++)
            {
                threads[C] = new Thread(new DDThread(C,numCores,ret,src,M));
                threads[C].start();
            }

            // Pause the current thread until all threads are done.
            for (int C = 0; C < numCores; C++)
                threads[C].join();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return ret;
    }

}
