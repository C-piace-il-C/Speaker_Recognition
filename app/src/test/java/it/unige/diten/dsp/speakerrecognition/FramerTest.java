package it.unige.diten.dsp.speakerrecognition;

import junit.framework.TestCase;

public class FramerTest extends TestCase {
    static byte[] registration;

    public void setUp() throws Exception {

        super.setUp();

        registration = new byte[1000];

        for(int i=0;i<1000;i+=2) // pari = LSB
        {
            registration[i] = (byte) (i & 0x000000FF);
            registration[i+1] = (byte) (((i & 0x0000FF00) >> 8));
        }
    }
    public void nonGestire(Exception ex)
    {
    }
    public void testFramer()
    {
        try {
            Framer.readFromFile("C:\\Test\\pls.wav");
        }
        catch(Exception e)
        {
            nonGestire(e);
        }
    }

}