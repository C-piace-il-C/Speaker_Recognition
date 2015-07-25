package it.unige.diten.dsp.speakerrecognition;


public class FEThread implements Runnable
{
    private int threadNumber;
    private Frame[] framesPtr;
    private int threadCount;
    private double[][] MFCCPtr;
    private Periodogrammer periodogrammer;

    public FEThread(int threadNumber, int threadCount, Frame[] frames, double[][] MFCC)
    {
        this.threadNumber   = threadNumber;
        this.framesPtr      = frames;
        this.threadCount    = threadCount;
        this.MFCCPtr        = MFCC;
        this.periodogrammer = new Periodogrammer(frames[0].data.length);
    }

    public void run()
    {
        for (int frame = threadNumber; frame < framesPtr.length; frame += threadCount)
        {
            MFCCPtr[frame] = DCT.computeDCT(
                    Logarithmer.computeLogarithm(
                            MelScaler.extractMelEnergies(
                                    periodogrammer.computePeriodogram(
                                            framesPtr[frame]
                                    )
                            )
                    ), FeatureExtractor.MFCC_COUNT // Only keep the first MFCC_COUNT coefficients
            );
        }
    }
}
