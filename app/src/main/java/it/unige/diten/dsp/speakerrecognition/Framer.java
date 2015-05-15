package it.unige.diten.dsp.speakerrecognition;

import it.unige.diten.dsp.speakerrecognition.WavIO.WavIO;

public abstract class Framer {
    /// Duration of the single frame in ms
    private final static int FRAME_LENGTH_MS = 32;
    /// Expected sample rate in Hz
    private final static int SAMPLE_RATE = 8000;
    /// Number of samples in a single frame
    private final static int SAMPLES_IN_FRAME = SAMPLE_RATE * FRAME_LENGTH_MS / 1000;
    /// Frame overlap factor
    private final static float FRAME_OVERLAP_FACTOR = .75f;
    /// Number of Byte(s) per sample
    private final static int BPS = 2;
    /// Frame size in Byte(s)
    private final static int FRAME_BYTE_SIZE= SAMPLES_IN_FRAME * BPS;
    /// Distance to next frame in Byte (center)
    private final static int FRAME_BYTE_SPACING = (int)(FRAME_BYTE_SIZE * (1-FRAME_OVERLAP_FACTOR));


    /// Container for each frame
    private class Frame {
        short[] data = null;
    }
    /// Container for all frames
    private static Frame[] frames = null;

    /// Convert byte[] to short[] with zero-filling
    private static short[] ToShortArray (byte[] src, int pos_byte, int len) {

        short[] retV = new short[len];

        for (int i = 0; i < len * 2; i += 2) {
            // Zero-filling (DA MIGLIORAREEEEEEEEEEEEEEEEEEEEEEEEEEEE)
            if (pos_byte + i + 1 >= src.length)
                retV[i / 2] = 0;
            else { // Copy two bytes into one short
                retV[i / 2] =   (short) ((src[pos_byte + i])            & 0x00FF); //LSB
                retV[i / 2] +=  (short) ((src[pos_byte + i + 1] << 8)   & 0xFF00); //MSB
            }
        }

        return (retV);
    }

    /// Read WAVE file from SDCard
    public static void ReadFromFile(String fileName) throws Exception {
        WavIO readWAV = new WavIO(fileName);
        readWAV.read();

        if (readWAV.GetSampleRate() != SAMPLE_RATE)
            throw new Exception("ReadFromFile: Invalid sample rate!");
        // Clear old data
        frames = null;

        int frameCount = readWAV.myData.length / FRAME_BYTE_SPACING;
        frames = new Frame[frameCount]; // Da correggere

        for (int i = 0; i < frames.length; i++)
            frames[i].data = ToShortArray(readWAV.myData, i * FRAME_BYTE_SPACING, SAMPLES_IN_FRAME);
    }
}