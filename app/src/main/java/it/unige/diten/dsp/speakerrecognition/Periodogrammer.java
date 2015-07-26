// Completely tested

package it.unige.diten.dsp.speakerrecognition;

/**
 * Periodogrammer
 * This class computes the periodogram of a frame
 */
public class Periodogrammer
{
    private Complex[] ft;
    private double[] hammingWindow;

    public Periodogrammer(int size)
    {
        hammingWindow = new double[size];
        // Create an hamming window and then computeWindowedDFT
        for( int C = 0; C < size; C++)
            hammingWindow[C] = 0.54-0.46*Math.cos((2.0*Math.PI*(double)C)/((double)size-1.0));

        ft = new Complex[size];
        // Initialize ft
        for(int C = 0; C < ft.length; C++)
            ft[C] = new Complex();
    }

    /**
     * @brief   Computes the periodogram of frame.
     *          A periodogram is a real sequence of the same length of frame (N)
     *          periodogram[i] = 1/N * |F[i]|^2, F[i] being the DFT of frame.
     * @param   frame   The frame.
     * @return  The periodogram in a double array.
     */
    public double[] computePeriodogram (Frame frame)
    {
        int size = frame.data.length;

        // Initialize return value
        double[] periodogram = new double[size];

        double N = (double)size; // Convert once, use many times! ~Xat



        // Compute periodogram
        for (int C = 0; C < size; C++)
            periodogram[C] = ft[C].getSquareLength() / N;

        return (periodogram);
    }
}
