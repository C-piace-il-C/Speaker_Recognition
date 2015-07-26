// Completely tested

package it.unige.diten.dsp.speakerrecognition;

/**
 * Complex
 * Class to handle complex numbers
 */
public class Complex {
    public double Re;
    public double Im;

    public Complex() {
        Re = .0;
        Im = .0;
    }

    public Complex(double re, double im)
    {
        Re = re;
        Im = im;
    }

    public double getLength()
    {
        return (Math.sqrt(Re * Re + Im * Im));
    }
    public double getSquareLength()
    {
        return (Re * Re + Im * Im);
    }
    public double getPhase()
    {
        return (Math.atan2(Im, Re));
    }

    public void addPhase( double relPhase )
    {
        double len = getLength();
        double angle = getPhase()+relPhase;
        Re = len * Math.cos(angle);
        Im = len * Math.sin(angle);
    }
}
