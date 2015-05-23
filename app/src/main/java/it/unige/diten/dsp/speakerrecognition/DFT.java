package it.unige.diten.dsp.speakerrecognition;

/**
 * DFT
 * Classe astratta che si occupa di calcolare la DFT di una sequenza di double.
 */

public abstract class DFT {

    public static void computeDFT(double[] src, Complex[] dest) {
        
        if(dest.length < src.length)
            return;
        
        double angle;

        for(int k=0; k<src.length; k++) {
            dest[k].re = .0;
            dest[k].im = .0;
            for (int n = 0; n < src.length; n++) {
                angle = -2*Math.PI*(double)k*(double)n/(double)src.length;

                dest[k].re += src[n] * Math.cos(angle);
                dest[k].im += src[n] * Math.sin(angle);
            }
        }
    }
}
