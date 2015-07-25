package it.unige.diten.dsp.speakerrecognition;

public class DDThread implements Runnable
{
    // threadNumber ranges from 0 to threadCount-1
    private int threadNumber;
    private int threadCount;
    private double[][] DDPtr;
    private double[][] MFCCPtr;
    private int precision;

    public DDThread(int threadNumber, int threadCount, double[][] DD, double[][] MFCC, int precision)
    {
        this.threadNumber   = threadNumber;
        this.threadCount    = threadCount;
        this.DDPtr          = DD;
        this.MFCCPtr        = MFCC;
        this.precision      = precision;
    }

    public void run()
    {
        double denominator =
                Math.pow(2 * (precision * (precision + 1) * (2 * precision + 1)) / 6.0, 2);
        int frameCount = MFCCPtr.length;
        int featureCount = MFCCPtr[0].length;

        // Multithread: each thread works on different values of k.
        // Eventually, all values of k (from 0 to featureCount-1) are covered.
        for (int k = threadNumber; k < featureCount; k += threadCount)
        {
            // f: frame index
            for (int f = 0; f < frameCount; f++)
            {
                for (int m = -precision; m <= precision; m++)
                {
                    for (int n = -precision; n <= precision; n++)
                    {
                        // Boundaries check (this prevents IndexOutOfBoundException)
                        if ( (f + m + n >= 0) && (f + m + n < frameCount) )
                            DDPtr[f][k] += m * n * MFCCPtr[f + m + n][k];
                    }
                }
                DDPtr[f][k] /= denominator;
            }
        }
    }
}
