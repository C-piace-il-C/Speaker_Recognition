package it.unige.diten.dsp.speakerrecognition;

/**
 * DFT
 * Classe astratta che si occupa di calcolare la DFT di una sequenza di double.
 */

public abstract class DFT
{
    static final double precision = Math.pow(10, -6);
    /**
     * @brief   compute the DFT of the real sequence src and saves it in dest.
     * @param src   The source sequence.
     * @param dest  The destination sequence. This must be large enough to
     *              contain src.
     */
    public static void computeDFT(double[] src, Complex[] dest) 
    {
        if(dest.length < src.length)
            return;
        
        double angle;
        int N = src.length;

        for(int k = 0; k < N; k++) {
            dest[k].re = 0;
            dest[k].im = 0;
            for (int n = 0; n < N; n++) {
                angle = -2*Math.PI*(double)k*(double)n/(double)N;

                dest[k].re += src[n] * Math.cos(angle);
                dest[k].im += src[n] * Math.sin(angle);
            }
            // Under a certain value we can consider it as if it were 0
            if(Math.abs(dest[k].re) < precision) dest[k].re = 0;
            if(Math.abs(dest[k].im) < precision) dest[k].im = 0;
        }
    }
    /**
     * @brief   compute the real part of the IDFT of sequence src and saves it in dest.
     * @param src   The source sequence.
     * @param dest  The destination sequence. This must be large enough to
     *              contain src.
     */
    public static void computeIDFT(Complex[] src, double[] dest)
    {
        if(dest.length < src.length)
            return;

        int N = dest.length;

        for(int n = 0; n < N; n++)
        {
            dest[n] = 0;
            for(int k = 0; k < N; k++)
            {
                // src[k] = src[k] * e^(jnk2PI/N) => solo uno sfasamento
                src[k].addPhase(2.0*Math.PI / (double)N*(double)k*(double)n);
                // add the real part
                dest[n] += src[k].re;
            }
            // Scaling factor
            dest[n] /= (double)N;
            // If the result has too much precision, round it
            if(Math.abs(Math.round(dest[n]) - dest[n]) < precision) dest[n] = Math.round(dest[n]);
        }
    }
}
