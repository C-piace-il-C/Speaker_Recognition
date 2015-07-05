package it.unige.diten.dsp.speakerrecognition;

/**
 * Periodogrammer
 * Classe per calcolare il periodogramma di un frame
 */
public abstract class Periodogrammer {

    
    private static Complex[] ft = new Complex[Framer.SAMPLES_IN_FRAME];
    private static double[] hammingWindow = new double[Framer.SAMPLES_IN_FRAME];


    public Periodogrammer()
    {
        Initialize();
    }

    private static void Initialize()
    {
        // Create an hamming window and then computeWindowedDFT
        // Il primo campione viene quasi annullato, quello in mezzo (N/2) passa inalterato, l'ultimo
        for( int C = 0; C < Framer.SAMPLES_IN_FRAME; C++) // questa parte andrebbe spostata in un initialize
            hammingWindow[C] = 0.54-0.46*Math.cos((2.0*Math.PI*(double)C)/((double)Framer.SAMPLES_IN_FRAME-1.0));
    }

    public static double[] computePeriodogram (Frame frame)
    {
        // Initialize return value
        double[] periodogram = new double[Framer.SAMPLES_IN_FRAME];

        double N = (double)Framer.SAMPLES_IN_FRAME; // converti una sola volta, usalo tante! ~Xat

        // Compute DFT \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        ////////////////////////////////////////////////////////////////////////////////////////////
        double[]  windowedFrame = new double[Framer.SAMPLES_IN_FRAME];
        for( int C = 0; C < Framer.SAMPLES_IN_FRAME; C++)
            windowedFrame[C] = frame.data[C] * hammingWindow[C];

        DFT.computeDFT(
                windowedFrame,  // src
                ft              // dest
        );

        // Compute periodogram \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        ////////////////////////////////////////////////////////////////////////////////////////////

        // Please do not rename the for index variable, we need C++ to get things work properly.
        for (int C = 0; C < Framer.SAMPLES_IN_FRAME; C++)
            periodogram[C] = (1.0 / N) * ft[C].getSquareLength();

        return (periodogram);
    }
}
