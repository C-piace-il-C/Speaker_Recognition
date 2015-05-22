package it.unige.diten.dsp.speakerrecognition;

/**
 * Periodogrammer
 * Classe per calcolare il periodogramma di un frame
 */
public abstract class Periodogrammer {

    private static Complex[] ft = new Comples[Framer.SAMPLES_IN_FRAME];

    public static short[] CalculatePeriodgram (Frame frame) {
        // Init return value
        short[] periodogram = new short[Framer.SAMPLES_IN_FRAME];

        // Compute DFT
        DFT.computeDFT(frame.data,ft);

        // Compute periodogram
        // Please do not rename the for index variable, we need C++ to get things work properly.
        for (int C = 0; C < Framer.SAMPLES_IN_FRAME; C++){
            periodogram[C] = (short)((1.0f / (float)Framer.SAMPLES_IN_FRAME) *
                    ft[C].GetSquareLength());
        }

        return (periodogram);
    }
}
