package it.unige.diten.dsp.speakerrecognition;

/**
 * Range holder.
 */
public class Range
{
    public double[] y_min;
    public double[] y_max;

    /**
     * Default constructor.
     */
    public Range()
    {
        y_min = null;
        y_max = null;
    }

    /**
     * Create a Range object given size.
     * @param size Array size.
     */
    public Range (int size)
    {
        y_min = new double[size];
        y_max = new double[size];
    }
}
