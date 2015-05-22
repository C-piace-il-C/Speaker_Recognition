package it.unige.diten.dsp.speakerrecognition;

/**
 * Complex
 * Tipo per i numeri complessi. Viene utilizzato durante l'elaborazione dei samples
 * a partire DFT.
 */
public class Complex {
    public double re, im;
    public double GetLength()
    {
        return(Math.sqrt(re*re + im*im));
    }
    public double GetSquareLength()
    {
        return(re*re + im*im);
    }
    // Questa è da correggere
    public double GetPhase()
    {
        return(Math.atan(im/re));
    }
}
