// TESTATA COMPLETAMENTE

package it.unige.diten.dsp.speakerrecognition;

/**
 * DFT
 * Abstract class to compute the DFT of a real sequence.
 */

public abstract class DFT
{
    /**
     * @brief       compute the DFT of the real sequence src and saves it in dest.
     * @param src   The source sequence.
     * @param dest  The destination sequence. This must be large enough to
     *              contain src, otherwise IndexOutOfBoundExceptions will eventually occur.
     */
    public static void computeDFT(double[] src, Complex[] dest) 
    {
        if(dest.length < src.length)
            return;
        
        double angle;
        int N = src.length;
        double angleFactor = -2.0*Math.PI/(double)N;
        for(int k = 0; k < N; k++)
        {
            dest[k].Re = .0;
            dest[k].Im = .0;
            for (int n = 0; n < N; n++)
            {
                //angle = -2.0*Math.PI*(double)k*(double)n/(double)N;
                angle = angleFactor*(double)k*(double)n;
                dest[k].Re += src[n] * Math.cos(angle);
                dest[k].Im += src[n] * Math.sin(angle);
            }
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
                dest[n] += src[k].Re;
            }
            // Scaling factor
            dest[n] /= (double)N;
            // If the result has too much precision, round it
        }
    }
}
