package it.unige.diten.dsp.speakerrecognition;

/**
 *
 */

public abstract class DFT {

    public static Complex[] computeDFT(short[] src) {
        Complex[] mDFT = new Complex[src.length];
        double angle;

        for(int k=0; k<src.length; k++) {
            mDFT[k].re = .0;
            mDFT[k].im = .0;

            for (int n = 0; n < src.length; n++) {
                angle = -2*Math.PI*(double)k*(double)n/(double)src.length;

                mDFT[k].re += ((double)src[n]) * Math.cos(angle);
                mDFT[k].im += ((double)src[n]) * Math.sin(angle);
            }
        }

        return(mDFT);
    }
}
