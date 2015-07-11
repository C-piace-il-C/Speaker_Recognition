package it.unige.diten.dsp.speakerrecognition;

/**
 * Created by PCAndreaLu on 11/07/2015.
 */
public class FEThread implements Runnable
{
    private int threadNumber;
    private Frame[] framesPtr;
    private int threadCount;
    private double[][] MFCCPtr;

    public FEThread(int threadNumber, int threadCount, Frame[] frames, double[][] MFCC)
    {
        this.threadNumber   = threadNumber;
        this.framesPtr      = frames;
        this.threadCount    = threadCount;
        this.MFCCPtr        = MFCC;
    }

    public void run()
    {
        for (int frame = threadNumber; frame < framesPtr.length; frame += threadCount)
        {
            MFCCPtr[frame] = DCT.computeDCT(
                    Logarithmer.computeLogarithm(
                            MelScaler.extractMelEnergies(
                                    Periodogrammer.computePeriodogram(
                                            framesPtr[frame]
                                    )
                            )
                    ), FeatureExtractor.MFCC_COUNT // Only keep the first MFCC_COUNT coefficients of the resulting DCT sequence
            );
        }
    }
}
