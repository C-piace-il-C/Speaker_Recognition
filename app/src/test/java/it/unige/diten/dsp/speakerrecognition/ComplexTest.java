package it.unige.diten.dsp.speakerrecognition;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ComplexTest extends TestCase {

    static Complex comp = new Complex();


    public void setUp() throws Exception {
        super.setUp();
        comp.Re = 0;
        comp.Im = 0;
    }

    public void testGetLength() throws Exception {
        Assert.assertTrue(areEqual(0., comp.getLength()));
        Complex c = new Complex(10.0,-3.0);
        Assert.assertTrue(areEqual(10.4403065089,c.getLength()));
    }

    public void testGetSquareLength() throws Exception {
        Assert.assertTrue(areEqual(0., comp.getSquareLength()));
        Complex c = new Complex(10.0,-3.0);
        Assert.assertTrue(areEqual(109.0,c.getSquareLength()));
    }


    public void testArr() throws Exception
    {
        int count = 10;

        Complex[] comp = new Complex[count];
        for(int i=0;i<10;i++)
            comp[i] = new Complex((double)i,0.0);

        for(int i=0;i<10;i++)
            Assert.assertTrue(areEqual(comp[i].Re,(double)i));
    }

    private boolean areEqual(double a, double b)
    {
        double abs = a-b < 0 ? (b-a) : (a-b);
        return(abs <= 0.1);
    }

}