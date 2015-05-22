package it.unige.diten.dsp.speakerrecognition;

/**
 * DFT
 * Classe astratta che si occupa di calcolare la DFT di una sequenza di short.
 */

public abstract class DFT {

    public static void computeDFT(short[] src, Complex[] dest) {
        
        if(dest.length < src.length)
            return;
        
        double angle;

        for(int k=0; k<src.length; k++) {
            mDFT[k].re = .0;
            mDFT[k].im = .0;
            for (int n = 0; n < src.length; n++) {
                angle = -2*Math.PI*(double)k*(double)n/(double)src.length;

                dest[k].re += ((double)src[n]) * Math.cos(angle);
                dest[k].im += ((double)src[n]) * Math.sin(angle);
            }
        }
    }
}
