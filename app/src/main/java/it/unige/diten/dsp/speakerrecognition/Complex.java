package it.unige.diten.dsp.speakerrecognition;

/**
 * Complex.
 * Class to handle complex numbers.
 */
public class Complex
{
    public double Re;
    public double Im;

    /**
     * Create a new complex (0 + i0).
     */
    public Complex()
    {
        Re = .0;
        Im = .0;
    }

    /**
     * Create a complex given Real and Imaginary part.
     * @param Re    Real part.
     * @param Im    Imaginary part.
     */
    public Complex(double Re, double Im)
    {
        this.Re = Re;
        this.Im = Im;
    }

    /**
     * Get vector length.
     * @return  Length.
     */
    public double getLength()
    {
        return (Math.sqrt(Re * Re + Im * Im));
    }

    /**
     * Get vector square length.
     * @return  Squared length.
     */
    public double getSquareLength()
    {
        return (Re * Re + Im * Im);
    }

    /**
     * Get vector phase.
     * @return  Phase.
     */
    public double getPhase()
    {
        return (Math.atan2(Im, Re));
    }

    /**
     * Add vector phase to existing.
     * @param relPhase  Phase in radiant.
     */
    public void addPhase(double relPhase)
    {
        double len = getLength();
        double angle = getPhase() + relPhase;

        Re = len * Math.cos(angle);
        Im = len * Math.sin(angle);
    }

    /**
     * Sum two Complex without modify objects (this + b).
     * @param b Complex to sum.
     * @return  Sum.
     */
    public Complex plus(Complex b)
    {
        Complex a = this;
        double re = a.Re + b.Re;
        double im = a.Im + b.Im;
        return new Complex(re, im);
    }

    /**
     * Subtract two Complex without modify objects (this - b).
     * @param b Complex to subtract.
     * @return  Subtract.
     */
    public Complex minus(Complex b)
    {
        Complex a = this;
        double re = a.Re - b.Re;
        double im = a.Im - b.Im;
        return new Complex(re, im);
    }

    /**
     * Multiply two Complex without modify objects (this * b).
     * @param b Complex to multiply.
     * @return  Product.
     */
    public Complex times(Complex b)
    {
        Complex a = this;
        double re = (a.Re * b.Re) - (a.Im * b.Im);
        double im = (a.Re * b.Im) + (a.Im * b.Re);
        return new Complex(re, im);
    }

    /**
     * Scalar multiplication without modify objects (this * alpha).
     * @param alpha Scalar to multiply.
     * @return      Product.
     */
    public Complex times(double alpha)
    {
        return new Complex(alpha * Re, alpha * Im);
    }

    /**
     * Conjugate Complex without modify objects.
     * @return Conjugate.
     */
    public Complex conjugate()
    {
        return new Complex(Re, -Im);
    }
}
