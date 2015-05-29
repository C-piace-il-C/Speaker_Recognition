package it.unige.diten.dsp.speakerrecognition;

public class Logarithmer
{
    public static double[] computeLogarithm(double[] src)
    {
        for(int C = 0; C < src.length; C++ )
            src[C] = Math.log(src[C]);
        return( src );
    }
}
