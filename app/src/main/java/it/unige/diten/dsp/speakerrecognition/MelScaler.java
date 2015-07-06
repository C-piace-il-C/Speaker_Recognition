// DA TESTARE

package it.unige.diten.dsp.speakerrecognition;

import java.io.FileWriter;
import java.io.IOException;
/**
 * <summary>
 *     Input: Frame(s) array.
 * </summary>
 */
// Abstract here specifies MelScaler cannot be instantiated
public abstract class MelScaler
{
    public final static double  START_FREQ      = 300.0;
    public final static double  END_FREQ        = (double)Framer.SAMPLE_RATE / 2.0;
    public final static int     FILTERBANK_SIZE = 5;       // Number of filters.
    public final static int     FILTER_SIZE     = 1000;//Framer.SAMPLES_IN_FRAME;

    private static boolean      initialized     = false;    // Flag.

    private static double[][]   filterBank      = null;     // Holder for filterbanks.

    /**
     * Compute the filterbank matrix (set of triangle filter vectors)
     */
    private static void Initialize()
    {
        double[] filterFreq;

        // NOP number of positions
        /**
         * 1.   Calcolo delle frequenze che compongono i filtri
         *      le frequenze sono inserite linearmente in mels e riconvertite in hertz
         *      ottenendo un effetto logaritmico.
         *
         * 2.   Calcolo dei vettori di filterBank
         *      Ogni vettore del filterbank è nullo escluso il triangolo centrato alla
         *      frequenza corrispondente.
         */

        // Filter position initialization (evenly spaced in Mel).
        int NOPs = FILTERBANK_SIZE + 2;
        filterFreq = new double[NOPs];

        double step = (melScale(END_FREQ) - melScale(START_FREQ)) / (NOPs - 1.0);
        filterFreq[0] = melScale(START_FREQ);

        for( int C = 1; C < NOPs; C++)
        {
            // Starting from 1, find position for next frequency.
            filterFreq[C]        = filterFreq[C - 1] + step;
            // From Mel to Hertz.
            filterFreq[C - 1]    = IMelScale(filterFreq[C - 1]);
        }
        // Last conversion Mel -> Hertz.
        filterFreq[NOPs - 1] = END_FREQ; //IMelScale(filterFreq[NOPs - 1])

        // stepFreq so that maximum freq is associated to N/2  ( MAX_FREQ / (N/2) )
        double stepFreq = END_FREQ * 2.0 / ((double)FILTER_SIZE - 1.0);
        // L'indice dell'ultimo campione del filtro è N-1 quindi bisogna dividere per N-1, non per N
        // infatti la frequenza associata sarà come (N-1)*step = SAMPLE_RATE*2

        /* filterBank set-up - filterBank[N][F]
        *   - N: filter number
        *   - F: filter vector component
        */
        filterBank = new double[FILTERBANK_SIZE][FILTER_SIZE];
        for(int N = 0; N < FILTERBANK_SIZE; N++)
        {
            // currentFreq is the frequency of the F-th component of the filter vector
            double currentFreq = .0;
            double cnt_left = 0;
            double cnt_right = 0;


            double centralFreq = filterFreq[N + 1];//(filterFreq[N + 1] - filterFreq[N]) / 2.0 + filterFreq[N];

            for(int T=0;T<FILTER_SIZE; T++)
            {
                if(filterFreq[N] <= currentFreq && currentFreq <= centralFreq)          // Left.
                    cnt_left++;
                else if( centralFreq < currentFreq && currentFreq <= filterFreq[N + 2])  // Right.
                    cnt_right++;

                currentFreq += stepFreq;
            }

            currentFreq = 0.0;
            double slopeLeft = 1.0 / (cnt_left);
            double slopeRight = 1.0 / (cnt_right);

            cnt_left = 0;
            cnt_right = 0;

            for(int F = 0; F < FILTER_SIZE; F++)
            {

                // Triangle set-up.
                if(filterFreq[N] <= currentFreq && currentFreq <= centralFreq)          // Left.
                    filterBank[N][F] = slopeLeft * (cnt_left++);
                else if( centralFreq < currentFreq && currentFreq <= filterFreq[N + 2])  // Right.
                    filterBank[N][F] = 1.0 - slopeRight * (cnt_right++);
                else                                                                    // Out.
                    filterBank[N][F] = .0;


                currentFreq += stepFreq;
            }
        }

        // Stampo su file testuale i vettori
        try {
            FileWriter writer = new FileWriter("C:\\Test\\test.txt", true);
            for(int N = 0; N < FILTERBANK_SIZE; N++)
            {
                writer.write("% vector" + Integer.toString(N));
                writer.write("\r\nhold on;");
                for(int F = 1; F < FILTER_SIZE; F++)
                {
                    writer.write(
                            "drawLine([" +
                            Integer.toString(F-1) + " " +
                            Double.toString(filterBank[N][F-1]) + "],[" +
                            Integer.toString(F) + " " +
                            Double.toString(filterBank[N][F]) + "],'red');\r\n"
                    );
                }
            }
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @param periodogram double array with periodogram computed by Periodogrammer
     * @return array with Mel energies
     */
    public static double[] extractMelEnergies(double[] periodogram)
    {
        if(!initialized)
        {
            Initialize();
            initialized = true;
        }

        double[] energies = new double[FILTERBANK_SIZE];

        for(int C = 0; C < FILTERBANK_SIZE; C++)
        {
            energies[C] = .0;

            for(int i = 0; i < FILTER_SIZE; i++)
            {
                energies[C] += 2.0 * periodogram[i] * filterBank[C][i];
            }
        }

        return energies;
    }

    private static double melScale(double f)
    {
        // Natural logarithm of input.
        return (1125.0 * Math.log(1.0 + (f / 700.0)));
    }


    private static double IMelScale(double m)
    {
        // Inverse mel scale.
        return (700.0 * (Math.exp(m / 1125.0) - 1));
    }
}
