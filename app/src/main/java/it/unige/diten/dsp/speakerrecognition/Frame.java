package it.unige.diten.dsp.speakerrecognition;

/**
 * Frame
 * E' solo il tipo contenitore dei frame estratti da un file WAV.
 * Contiene un array di short con i samples del frame.
 * short perche' i samples sono per specifica a 16 bit.
 */
public class Frame {
    public double[] data = null;
}
