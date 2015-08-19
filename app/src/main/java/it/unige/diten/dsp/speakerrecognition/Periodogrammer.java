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
    private TransformSelector transformSelector;

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

        // TODO transformSelector = MainActivity.transformType (omessa perché altrimenti non funziona il debug unit test)
        transformSelector = TransformSelector.TT_DFT; //MainActivity.transformType;
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

        // Compute DFT of windowed sequence

        switch (transformSelector) {
            case TT_DFT:
                double[]  windowedFrame = new double[size];

                for( int C = 0; C < size; C++) {
                    windowedFrame[C] = frame.data[C] * hammingWindow[C];
                }

                DFT.computeDFT(
                        windowedFrame,  // src
                        ft              // dest
                );

                break;

            case TT_FFT:
                Complex[] cWindowedFrame = new Complex[size];

                for( int C = 0; C < size; C++)
                {
                    cWindowedFrame[C] = new Complex();
                    cWindowedFrame[C].Re = frame.data[C] * hammingWindow[C];
                    cWindowedFrame[C].Im = 0;
                }

                ft = FFT.fft(cWindowedFrame);

                break;


            default:
                throw new IllegalStateException();
        }

        // usa la trasformata per calcolare energie..


        // Compute periodogram
        for (int C = 0; C < size; C++)
            periodogram[C] = ft[C].getSquareLength() / N;

        return (periodogram);
    }
}
