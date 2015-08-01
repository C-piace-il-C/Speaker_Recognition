//  Completely tested

package it.unige.diten.dsp.speakerrecognition;

/**
 * <summary>
 *     Input: Frame(s) array.
 * </summary>
 */
// Abstract here specifies MelScaler cannot be instantiated
public abstract class MelScaler
{
    /// Frequency range for the energies extraction
    public final static double  START_FREQ      = 300.0;
    public final static double  END_FREQ        = Framer.SAMPLE_RATE / 2.0;
    /// Number of filters in the filterbank
    public final static int     FILTERBANK_SIZE = 26;
    /// Size in samples of a single filter
    public final static int     FILTER_SIZE     = Framer.SAMPLES_IN_FRAME;
    /// This allows one-time initialization without a constructor
    private static boolean      initialized     = false;
    /// The filterbank
    private static double[][]   filterBank      = null;

    /**
     * Compute the filterbank matrix (set of triangle filter vectors)
     */
    private static void Initialize()
    {
        double[] filterFreq;

        /**
         * 1.   Calcolo delle frequenze che compongono i filtri
         *      le frequenze sono inserite linearmente in mels e riconvertite in hertz
         *      ottenendo un effetto logaritmico.
         *
         * 2.   Calcolo dei vettori di filterBank
         *      Ogni vettore del filterbank è nullo escluso il triangolo centrato alla
         *      frequenza corrispondente.
         */

        // NOP number of positions
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
        filterFreq[NOPs - 1] = END_FREQ;

        // L'indice dell'ultimo campione del filtro è N-1 quindi bisogna dividere per N-1, non per N
        // infatti la frequenza associata sarà come (N-1)*step = SAMPLE_RATE*2

        /* filterBank set-up - filterBank[N][F]
        *   - N: filter number
        *   - F: filter vector component
        */

        filterBank = new double[FILTERBANK_SIZE][FILTER_SIZE];

        // stepFreq so that maximum freq is associated to N/2  ( MAX_FREQ / (N/2) )
        double stepFreq = END_FREQ * 2.0 / ((double)FILTER_SIZE - 1.0);

        for(int N = 0; N < FILTERBANK_SIZE; N++)
        {
            // currentFreq is the frequency of the F-th component of the filter vector
            double currentFreq = .0;
            double cntLeft     = .0;
            double cntRight    = .0;

            double centralFreq = filterFreq[N + 1];

            for(int T = 0; T < FILTER_SIZE; T++)
            {
                if(filterFreq[N] <= currentFreq && currentFreq <= centralFreq)             // Left.
                    cntLeft++;
                else if( centralFreq < currentFreq && currentFreq <= filterFreq[N + 2])    // Right.
                    cntRight++;

                currentFreq += stepFreq;
            }

            currentFreq       = .0;
            double slopeLeft  = 1.0 / (cntLeft);
            double slopeRight = 1.0 / (cntRight);

            cntLeft           = .0;
            cntRight          = .0;

            for(int F = 0; F < FILTER_SIZE; F++)
            {
                // Triangle set-up.
                if(filterFreq[N] <= currentFreq && currentFreq <= centralFreq)           // Left.
                    filterBank[N][F] = slopeLeft * (cntLeft++);
                else if( centralFreq < currentFreq && currentFreq <= filterFreq[N + 2])  // Right.
                    filterBank[N][F] = 1.0 - slopeRight * (cntRight++);
                else                                                                     // Out.
                    filterBank[N][F] = .0;

                currentFreq += stepFreq;
            }
        }
    }

    /**
     * @brief   Extract Mel energies from a periodogram. The energies are then returned as a double
     *          array.
     * @param   periodogram double array with the periodogram computed by Periodogrammer
     * @return  array with Mel energies
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
                energies[C] += periodogram[i] * filterBank[C][i];
            }
            // 2.0 because of frequency symmetry caused by real input.
            // energies[C] *= 2.0;
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
        return (700.0 * (Math.exp(m / 1125.0) - 1.0));
    }
}
