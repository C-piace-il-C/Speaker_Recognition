package it.unige.diten.dsp.speakerrecognition;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Created by doddo on 7/4/15.
 */
public class ComplexTest extends TestCase {

    static Complex comp = new Complex();

    public void setUp() throws Exception {
        super.setUp();
        comp.re = 0;
        comp.im = 0;
    }

    public void testGetLength() throws Exception {
        Assert.assertEquals(0., comp.getLength());
    }

    public void testGetSquareLength() throws Exception {
        Assert.assertEquals(0., comp.getSquareLength());
    }
}