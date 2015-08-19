package it.unige.diten.dsp.speakerrecognition;

public abstract class FCleaner
{
    /** Calculates the energy inside a frequency interval
     * \p fs        sample frequency
     * \p fmin      minimum frequency
     * \p fmax      maximum frequency
     * \p complex   it's difficult
     **/

    public static double extractFreqEnergy(Complex[] ft, int fs, int fmin, int fmax)
    {
        double energy = .0;
        int iMin, iMax;

        iMin = clip2(0, Framer.SAMPLES_IN_FRAME-1, fmin / fs * 512);
        iMax = clip2(0, Framer.SAMPLES_IN_FRAME-1, fmax / fs * 512);

        for(int C = iMin; C <= iMax; C++)
            energy += ft[C].getSquareLength();

        return(energy);
    }

    public static double[] filterSeq(Complex[] seqFt, Complex[] hFt)
    {
        double[] filteredSeq = null;

        int len = seqFt.length;
        if(len != hFt.length )
            return filteredSeq;

        filteredSeq = new double[len];

        Complex[] filteredFt = new Complex[len];
        for(int C = 0; C < len; C++) {
            filteredFt[C] = seqFt[C].times(hFt[C]);
        }

        DFT.computeIDFT(filteredFt, filteredSeq); // quali frame

        return filteredSeq;
    }
    private static int clip2(int min, int max, int value)
    {
        return( value < min ? min : ( value > max ? max : value ) );
    }
}
