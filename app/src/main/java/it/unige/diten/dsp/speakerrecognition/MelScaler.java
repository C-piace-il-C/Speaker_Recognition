// DA TESTARE

package it.unige.diten.dsp.speakerrecognition;

/**
 * <summary>
 *     Input: Frame(s) array.
 * </summary>
 */
public abstract class MelScaler {
    // Abstract here specifies MelScaler cannot be instantiated

    // final specifies the variable i
    public final static double  START_FREQ  = 300.0;
    public final static double  END_FREQ    = 8000.0;
    // Number of filters
    public final static int FILTERBANK_SIZE = 26;


    // Calculate filterbanks
    private static double[] filterFrequencies;
    private static double[][] filterBank;

    public MelScaler()
    {
        Initialize();
    }

    // Compute the filterbank matrix (set of triangle filter vectors)
    private static void Initialize()
    {
        // NOP number of positions
        /**
         * 1.   Calcolo delle frequenze che compongono i filtri
         *          le frequenze sono inserite linearmente in mels e riconvertite in hertz
         *          ottenendo un effetto logaritmico.
         * 2.   Calcolo dei vettori di filterBank
         *          Ogni vettore del filterbank è nullo escluso il triangolo centrato alla
         *          frequenza corrispondente.
         */
        int NOPs = (FILTERBANK_SIZE+2);
        filterFrequencies = new double[NOPs];

        filterFrequencies[0] = melScale(START_FREQ);
        double step = (END_FREQ-START_FREQ)/(NOPs-1.0);
        for( int C = 1; C < NOPs; C++ )
        {
            filterFrequencies[C] = filterFrequencies[C - 1] + step;
            filterFrequencies[C - 1] = IMelScale(filterFrequencies[C - 1]);
        }
        filterFrequencies[NOPs - 1] = IMelScale(filterFrequencies[NOPs - 1]);

        filterBank = new double[FILTERBANK_SIZE][Framer.SAMPLES_IN_FRAME];
        double currentFrequency;// = 0;
        double stepFrequency = (double)Framer.SAMPLE_RATE * 2.0 / (double)Framer.SAMPLES_IN_FRAME;
        double centralFreq, coeff;
        for( int F=0; F < FILTERBANK_SIZE; F++)
        {
            currentFrequency = 0;
            for( int C = 0; C < Framer.SAMPLES_IN_FRAME; C++)
            {
                coeff = 2.0 / (filterFrequencies[C + 1] - filterFrequencies[C]);
                centralFreq = (filterFrequencies[C + 1] - filterFrequencies[C]) / 2.0 + filterFrequencies[C];

                if( filterFrequencies[C] <= currentFrequency && currentFrequency <= centralFreq)
                    filterBank[F][C] = coeff*(currentFrequency-filterFrequencies[C]);
                else if(currentFrequency > centralFreq && currentFrequency <= filterFrequencies[C+1])
                    filterBank[F][C] = 1.0-coeff*(currentFrequency-centralFreq);
                else
                    filterBank[F][C] = .0;

                currentFrequency += stepFrequency;
            }
        }
    }

    /**
     * @param periodogram double array with periodogram computed by Periodogrammer
     * @return array with Mel energies
     */
    public static double[] extractMelEnergies(double[] periodogram)
    {

        double[] energies = new double[FILTERBANK_SIZE];

        for( int C = 0; C < FILTERBANK_SIZE; C++ )
        {
            energies[C] = .0;
            for( int i = 0; i < Framer.SAMPLES_IN_FRAME; i++ )
                energies[C] += periodogram[i] * filterBank[C][i];
        }

        return(energies);

    }

    private static double melScale(double f)
    {
        // Natural logarithm of input.
        return 1125.0*Math.log(1.0+f/700.0);
    }


    private static double IMelScale(double m)
    {
        // Inverse mel scale.
        return 700.0*(Math.exp(m/1125.0)-1);
    }
}
