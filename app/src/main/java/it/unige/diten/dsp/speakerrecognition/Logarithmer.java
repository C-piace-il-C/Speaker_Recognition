// Resta da controllare che sia giusto log in base e...

package it.unige.diten.dsp.speakerrecognition;

public class Logarithmer
{
    public static double[] computeLogarithm(double[] src)
    {
        for(int C = 0; C < src.length; C++)
        {
            src[C] = Math.log(src[C]); // Math.log of src (logaritmo naturale)
        }

        return src;
    }
}
