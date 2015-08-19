package it.unige.diten.dsp.speakerrecognition;

/**
 * Frame.
 * Container for frame data.
 */
public class Frame
{
    public double[]     data;
    public Complex[]    ft;

    /**
     * Default constructor.
     */
    public Frame()
    {
        data = null;
        ft   = null;
    }

    /**
     * Copy constructor.
     * @param f Frame to be copied.
     */
    public Frame(Frame f)
    {
        this.data = new double[f.data.length];
        this.ft   = new Complex[f.ft.length];

        System.arraycopy(f.data, 0, this.data, 0, f.data.length);
        System.arraycopy(f.ft, 0, this.ft, 0, f.ft.length);
    }
}
