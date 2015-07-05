// Completamente TESTATO (DOP NOP)

package it.unige.diten.dsp.speakerrecognition;

/**
 * Periodogrammer
 * Classe per calcolare il periodogramma di un frame
 */
public abstract class Periodogrammer {

    
    private static Complex[] ft;
    private static double[] hammingWindow;
    private static boolean initialized = false;

    public Periodogrammer()
    {

    }

    private static void Initialize(int size)
    {
        hammingWindow = new double[size];
        // Create an hamming window and then computeWindowedDFT
        // Il primo campione viene quasi annullato, quello in mezzo (N/2) passa inalterato, l'ultimo
        for( int C = 0; C < size; C++) // questa parte andrebbe spostata in un initialize
            hammingWindow[C] = 0.54-0.46*Math.cos((2.0*Math.PI*(double)C)/((double)size-1.0));

        ft = new Complex[size];
        // Initialize ft
        for(int C = 0; C < ft.length; C++)
            ft[C] = new Complex();
    }

    /**
     * @brief   Calcola il periodogramma di un frame (sequenza di double)
     *          Il periodogramma e' una sequenza reale lunga quanto quella in ingresso
     *          periodogram[i] = 1/N * |F[i]|^2
     *          essendo F[i] la DFT del frame, N la lunghezza della sequenza
     * @param   frame   Il frame del quale calcolare il periodogramma
     * @return  Double array contenente il periodogramma
     */
    public static double[] computePeriodogram (Frame frame)
    {
        int size = frame.data.length;

        if(!initialized)
            Initialize(size);


        // Initialize return value
        double[] periodogram = new double[size];

        double N = (double)size; // converti una sola volta, usalo tante! ~Xat

        // Compute DFT of windowed sequence \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        ////////////////////////////////////////////////////////////////////////////////////////////
        double[]  windowedFrame = new double[size];
        for( int C = 0; C < size; C++)
            windowedFrame[C] = frame.data[C] * hammingWindow[C];

        DFT.computeDFT(
                windowedFrame,  // src
                ft              // dest
        );

        // Compute periodogram \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        ////////////////////////////////////////////////////////////////////////////////////////////

        // Please do not rename the for index variable, we need C++ to get things work properly.
        for (int C = 0; C < size; C++)
            periodogram[C] = ft[C].getSquareLength() / N;

        return (periodogram);
    }
}
