package it.unige.diten.dsp.speakerrecognition;

/**
 * Log.
 * Perform natural logarithm on arrays.
 */
public class Log
{
    /**
     * Calculate natural logarithm on an array.
     * @param src   The array used to calculate ln.
     * @return      Same-length array containing ln of src.
     */
    public static double[] calculateArrayLog(final double[] src)
    {
        double[] ret = new double[src.length];

        for(int i = 0; i < src.length; i++)
        {
            ret[i] = Math.log(src[i]);
        }

        return ret;
    }
}
