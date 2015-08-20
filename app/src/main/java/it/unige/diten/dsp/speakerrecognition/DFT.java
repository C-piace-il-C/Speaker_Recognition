// testata completely

// TODO: Fai calcolare la DFT solo dei primi N/2 valori, gli altri sono ricavabili mediante proprietà di simmetria (2x speedup)
// TODO: Precalcola i fattori moltiplicativi nella DFT
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
        if (dest.length < src.length) { return; }

        int     N       = src.length;
        double  stride  = -2 * Math.PI / N;
        boolean Nparity = ((N & 1) == 0);

        int kMax = N >> 1;
        if(!Nparity)
            kMax++;

        for (int k = 0; k <= kMax; k++)
        {
            dest[k].Re  = .0;
            dest[k].Im  = .0;

            for (int n = 0; n < N; n++)
            {
                double angle = stride * k * n;

                dest[k].Re  += src[n] * Math.cos(angle);
                dest[k].Im  += src[n] * Math.sin(angle);
            }
        }

        double tempRe, tempIm;
        for(int k = kMax; k < N; k++)
        {
            int half = N/2;
            int refIndex = half - (k - half);

            dest[k].Re = dest[refIndex].Re;
            dest[k].Im = -dest[refIndex].Im;
            TextWriter.appendText("C:\\Tests\\debugDFT.txt", "k = " + String.valueOf(k) + " - ref: "+String.valueOf(refIndex));
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
        if (dest.length < src.length) { return; }

        int         N       = dest.length;
        double      stride  = 2 * Math.PI / N;
        double[]    lengths = new double[N];
        double[]    phases  = new double[N];

        for (int i = 0; i < N; i++)
        {
            dest[i]         = .0;
            lengths[i]      = src[i].getLength();
            phases[i]       = src[i].getPhase();
        }

        for (int n = 0; n < N; n++)
        {
            for (int k = 0; k < N; k++)
            {
                // Compute only the real part of the iDFT.
                dest[n]    += lengths[k] * Math.cos(stride * k * n + phases[k]);
            }

            // Scale output
            dest[n] /= N;
        }
    }
}
