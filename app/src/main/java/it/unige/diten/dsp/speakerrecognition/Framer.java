package it.unige.diten.dsp.speakerrecognition;

import it.unige.diten.dsp.speakerrecognition.WavIO.WavIO;

/**
 * Created by doddo on 5/13/15.
 */

public abstract class Framer {
    /// Duration of the single frame in ms
    private final static int FRAME_LENGTH = 32;
    /// Expected sample rate in Hz
    private final static int SAMPLE_RATE = 8000;
    /// Number of samples in a single frame
    private final static int SAMPLES_IN_FRAME = SAMPLE_RATE * FRAME_LENGTH / 1000;
    /// Number of bit per sample
    private final static int BPS = 16;
    /// Frame size in bytes
    private final static int FRAME_SIZE = SAMPLES_IN_FRAME * BPS / 8;

    /// Struct of frames
    private class Frame
    {
        short[] data;
    }

    /// Array of frames
    private static Frame[] frames = null;

    public static void ReadFromFile(String fileName) throws Exception
    {
        WavIO readWAV = new WavIO(fileName);
        readWAV.read();

        if(readWAV.GetSampleRate() != SAMPLE_RATE)
            throw(new Exception("666"));

        /// Cleaning old data
        frames = null;
        /*
        int padding = readWAV.myData.length % FRAME_SIZE;
        if(padding != 0) {

        }
        */
        int j = 0;
        for(;;) {
            frames[j].data = new short[FRAME_SIZE / 2];
            for (int i = 0; i < FRAME_SIZE; i+=2) {
                frames[j].data[i] = readWAV.myData[j * FRAME_SIZE + i];
                frames[j].data[i] += (short)(readWAV.myData[j * FRAME_SIZE + i + 1] << 8) & 0xff00;

            }
        }


    }
}
