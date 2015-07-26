package it.unige.diten.dsp.speakerrecognition;



public class DFTTestThread implements Runnable
{
    private Frame       framePtr;
    private Complex[]   outputPtr;
    public DFTTestThread(Frame framePtr, Complex[] outputPtr)
    {
        this.framePtr = framePtr;
        this.outputPtr = outputPtr;
    }

    public void run()
    {
        DFT.computeDFT(framePtr.data, outputPtr);
    }
}
