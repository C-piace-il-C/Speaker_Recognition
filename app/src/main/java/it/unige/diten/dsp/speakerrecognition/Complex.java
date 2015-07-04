package it.unige.diten.dsp.speakerrecognition;

/**
 * Complex
 * Tipo per i numeri complessi. Viene utilizzato durante l'elaborazione dei samples
 * a partire DFT.
 */
public class Complex {
    public double re, im;
    public double getLength()
    {
        return(Math.sqrt(re*re + im*im));
    }
    public double getSquareLength()
    {
        return(re*re + im*im);
    }
    public double getPhase()
    {
        return(Math.atan2(im,re));
    }

    public void addPhase( double relPhase )
    {
        double len = getLength();
        double angle = getPhase()+relPhase;
        re = len*Math.cos(angle);
        im = len*Math.sin(angle);
    }
}
