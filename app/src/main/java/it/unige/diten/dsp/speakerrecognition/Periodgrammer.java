package it.unige.diten.dsp.speakerrecognition;

/**
 * Input = short[]
 * Output = short[]
 */
public abstract class Periodgrammer {

    Complex[] ft;
    public short[] calculatePeriodgram (short[] src) {
        // Init return value
        short[] periodgram = new short[Framer.SAMPLES_IN_FRAME];

        // Compute DFT
        ft = DFT.computeDFT(src);

        // Compute periodogram
        for (int C = 0; C < Framer.SAMPLES_IN_FRAME; C++){

            periodgram[C] = (short)((1.0 / Framer.SAMPLES_IN_FRAME) * /*module*/
                    (ft[C].re*ft[C].re + ft[C].im*ft[C].im));
        }

        return (periodgram);
    }
}
