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
    // return a new Complex object whose value is (this + b)
    public Complex plus(Complex b) {
        Complex a = this;             // invoking object
        double real = a.Re + b.Re;
        double imag = a.Im + b.Im;
        return new Complex(real, imag);
    }
    // return a new Complex object whose value is (this - b)
    public Complex minus(Complex b)
    {
        Complex a = this;
        double real = a.Re - b.Re;
        double imag = a.Im - b.Im;
        return new Complex(real, imag);
    }
    // return a new Complex object whose value is (this * b)
    public Complex times(Complex b) {
        Complex a = this;
        double real = a.Re * b.Re - a.Im * b.Im;
        double imag = a.Re * b.Im + a.Im * b.Re;
        return new Complex(real, imag);
    }

    // scalar multiplication
    // return a new object whose value is (this * alpha)
    public Complex times(double alpha) {
        return new Complex(alpha * Re, alpha * Im);
    }

    // return a new Complex object whose value is the conjugate of this
    public Complex conjugate() {  return new Complex(Re, -Im); }
}
