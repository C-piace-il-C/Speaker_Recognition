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
        Assert.assertEquals(0., comp.getLength());
    }

    public void testGetSquareLength() throws Exception {
        Assert.assertEquals(0., comp.getSquareLength());
    }


    public void testArr() throws Exception {
        Complex[] comp = new Complex[10];
        Assert.assertEquals(comp[0].Re,0.0);
    }
}