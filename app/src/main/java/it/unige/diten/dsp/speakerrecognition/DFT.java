// testata completely

// TODO: Fai calcolare la DFT solo dei primi N/2 valori, gli altri sono ricavabili mediante proprietà di simmetria (2x speedup)
// TODO: Precalcola i fattori moltiplicativi nella DFT
// [
// TODO: Le operazioni tra i complessi vanno fatte inline e non chiamando le funzioni, tipo Complex.multiply.
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

        Complex temp = new Complex();
        Complex sum = new Complex();
        for(int n = 0; n < N; n++)
        {
            dest[n] = 0;
            sum.Re = 0;
            sum.Im = 0;
            for(int k = 0; k < N; k++)
            {
                temp.Re = src[k].Re;
                temp.Im = src[k].Im;
                // src[k] = src[k] * e^(jnk2PI/N) => solo uno sfasamento
                temp.addPhase(2.0*Math.PI / (double)N*(double)k*(double)n);
                // add the real part
                //dest[n] += temp[k].Re;
                sum.Re += temp.Re;
                sum.Im += temp.Im;
            }
            dest[n] = sum.Re;
            // Scaling factor
            dest[n] /= (double)N;
            // If the result has too much precision, round it
        }
    }
    /*public static void computeIDFT(Complex[] src, double[] dest)
    {
        if(dest.length < src.length) { return; }

        int N               = dest.length;
        double[] lengths    = new double[N];
        double[] phases     = new double[N];

        for (int i = 0; i < N; i++)
        {
            dest[i]         = .0;
            lengths[i]      = src[i].getLength();
            phases[i]       = src[i].getPhase();
        }

        for(int n = 0; n < N; n++)
        {
            for(int k = 0; k < N; k++)
            {
                dest[n]    += lengths[k] * Math.cos(2 * Math.PI / (N * k * n) + phases[k]);
            }

            // Scale output
            dest[n] /= N;
        }
    }*/
}
