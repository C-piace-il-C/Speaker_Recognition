package it.unige.diten.dsp.speakerrecognition;

public class DDThread implements Runnable
{
    private int threadNumber, threadCount;
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
        double adj = Math.pow(2 * (precision * (precision + 1) * (2 * precision + 1)) / 6.0, 2);

        for (int k = threadNumber; k < MFCCPtr[0].length; k += threadCount)
        {
            // f: frame index
            for (int f = 0; f < MFCCPtr.length; f++)
            {
                for (int m = -precision; m <= precision; m++)
                {
                    // meglio:
                    // for(int n = min; n <= max; n++) invece di un if dentro ogni iterazione del for
                    // visto che f+m+n >= 0, e quindi n >= -f -m, allora min = Math.max(-f-m,-M)
                    // visto che f+m+n < src.length, e quindi n < src.length - m - f, allora
                    //      max = Math.min(src.length - m - f, M)
                    int min = Math.max(-f -m, -precision);
                    int max = Math.min(MFCCPtr.length - m - f, precision);
                    for (int n = min; n <= max; n++)
                    {
                        // Check bounds
                        //if ((f + m + n >= 0) && (f + m + n < MFCCPtr.length))
                        DDPtr[f][k] += m * n *  MFCCPtr[f + m + n][k];
                    }
                }

                DDPtr[f][k] /= adj;
            }
        }
    }
}
