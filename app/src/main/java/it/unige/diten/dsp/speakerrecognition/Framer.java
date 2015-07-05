package it.unige.diten.dsp.speakerrecognition;

import it.unige.diten.dsp.speakerrecognition.WavIO.WavIO;

/**
 * Framer
 * E' una classe astratta che si occupa di leggere un file .WAV per poi suddividerlo in frames
 * secondo i parametri noti (public final static...)
 * Il codice dovrebbe adattarsi a tutti parametri (compreso BPS a partire dal commit corrente)
 */
public abstract class Framer {
    /// Duration of the single frame in ms
    public final static int FRAME_LENGTH_MS = 32;
    /// Expected sample rate in Hz
    public final static int SAMPLE_RATE = 8000;
    /// Number of samples in a single frame
    public final static int SAMPLES_IN_FRAME = SAMPLE_RATE * FRAME_LENGTH_MS / 1000;
    /// Frame overlap factor
    public final static float FRAME_OVERLAP_FACTOR = .75f;
    /// Number of Byte(s) per sample
    public final static int BPS = 2;
    /// Frame size in Bytes
    public final static int FRAME_BYTE_SIZE = SAMPLES_IN_FRAME * BPS;
    /// Distance in Bytes between the beginning of a frame and the following
    public final static int FRAME_BYTE_SPACING = (int) ((float) FRAME_BYTE_SIZE * (1.0f - FRAME_OVERLAP_FACTOR));

    /// Container for all frames
    private static Frame[] frames = null;

    /// Convert byte[] to short[] with zero-filling

    private static short[] toShortArray(byte[] src, int byte_offset, int len)
    {
        short[] shorts = new short[len];

        for (int i = 0; i < len * 2; i += 2)
        {
            // Zero-filling
            if (byte_offset + i + 1 >= src.length)
                shorts[i / 2] = 0;
            else {                                      // Copy two bytes into one short
                shorts[i / 2] = (short) ((src[byte_offset + i]) & 0x00FF); //LSB
                shorts[i / 2] += (short) ((src[byte_offset + i + 1] << 8) & 0xFF00); //MSB
            }
        }

        return (shorts);
    }

    /**
     * @brief   Converts a raw data source (array of bytes) to an array of double with zero-filling.
     *          The raw data source is expected to contain integer elements.
     *          by ALu. For info contact me at alu@cumallover.me.
     *          note: yet to be debugged
     * @param src Reference to the source.
     * @param byteOffset Index of the first byte read from src.
     * @param len        Number of elements to be read from src.
     * @param byteStride Size in bytes of a single elment of src.
     *                   note: byteStride must be in the range [1,8]
     */
    public static double[] toDoubleArray(byte[] src, int byteOffset, int len, int byteStride)
    {
        int size = len * byteStride;
        double[] retV = new double[size];

        int i;

        // Convert input to double.
        for (i = 0; (i < size) && (i + byteOffset < src.length); i++)
        {
            retV[i] = (double) ((src[byteOffset + i * byteStride]) & 0x00FF); //LSB
            retV[i] += (double) ((src[byteOffset + i * byteStride + 1] << 8) & 0xFF00); //MSB
        }

        // Zero-filling.
        while (i < size)
            retV[i++] = .0;

        return (retV);
    }

    /// Read WAVE file from SDCard
    public static void readFromFile(String fileName) throws Exception
    {
        WavIO readWAV = new WavIO(fileName);
        readWAV.read();

        if (readWAV.GetSampleRate() != SAMPLE_RATE)
            throw new Exception("Framer.readFromWAV: Invalid sample rate!");
        // Clear old data (garbage collector)
        frames = null;

        int frameCount = readWAV.myData.length / FRAME_BYTE_SPACING;
        frames = new Frame[frameCount];


        for (int C = 0; C < frameCount; C++) {
            frames[C] = new Frame();
            frames[C].data = toDoubleArray(
                    readWAV.myData,         // src
                    C * FRAME_BYTE_SPACING, // byte offset
                    SAMPLES_IN_FRAME,       // len
                    BPS                     // stride
            );
        }
    }

    /// Returns frame array
    public static Frame[] getFrames()
    {
        return (frames);
    }
}
