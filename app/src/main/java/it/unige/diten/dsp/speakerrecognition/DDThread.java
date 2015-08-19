package it.unige.diten.dsp.speakerrecognition;

/**
 * Delta-Deltas thread class.
 */
public class DDThread implements Runnable
{
    private int         threadNum;
    private int         threadCnt;
    private double[][]  DD_Matrix;
    private double[][]  MFCC_Matrix;
    private int         p;

    /**
     * DDThread constructor.
     * @param threadNum Number of thread, from 0 to 'threadCnt - 1'.
     * @param threadCnt Total number of threads.
     * @param DD        Delta-Delta matrix.
     * @param MFCC      MFCC matrix.
     * @param precision The precision of the Delta-Deltas.
     */
    public DDThread (int threadNum, int threadCnt, double[][] DD, double[][] MFCC, int precision)
    {
        this.threadNum   = threadNum;
        this.threadCnt   = threadCnt;
        this.DD_Matrix   = DD;
        this.MFCC_Matrix = MFCC;
        this.p           = precision;
    }

    public void run()
    {
        double adj        = Math.pow(2 * (p * (p + 1) * (2 * p + 1)) / 6.0, 2);
        int    frameCnt   = MFCC_Matrix.length;
        int    featureCnt = MFCC_Matrix[0].length;

        // Each thread works on different values of k (k: feature index).
        for (int k = threadNum; k < featureCnt; k += threadCnt)
        {
            // f: frame index.
            for (int f = 0; f < frameCnt; f++)
            {
                // Compute Delta-Delta calculation.
                for (int m = -p; m <= p; m++)
                {
                    for (int n = -p; n <= p; n++)
                    {
                        // Boundaries check (this prevents IndexOutOfBoundException)
                        if ((f + m + n >= 0) && (f + m + n < frameCnt)) {
                            DD_Matrix[f][k] += m * n * MFCC_Matrix[f + m + n][k];
                        }
                    }
                }

                // Scale factor.
                DD_Matrix[f][k] /= adj;
            }
        }
    }
}
