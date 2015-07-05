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
        short[] retV = new short[len];

        for (int i = 0; i < len * 2; i += 2)
        {
            // Zero-filling
            if (byte_offset + i + 1 >= src.length)
                retV[i / 2] = 0;
            else {                                      // Copy two bytes into one short
                retV[i / 2] =   (short) ((src[byte_offset + i])            & 0x00FF); //LSB
                retV[i / 2] +=  (short) ((src[byte_offset + i + 1] << 8)   & 0xFF00); //MSB
            }
        }

        return (retV);
    }

    /**
     * @brief   Converts a raw data source (array of bytes) to an array of double with zero-filling.
     *          The raw data source is expected to contain integer elements.
     *          by ALu. For info contact me at alu@cumallover.me.
     *          note: yet to be debugged
     * @param rawDataSrc Reference to the source.
     * @param byteOffset Index of the first byte read from rawDataSrc.
     * @param len        Number of elements to be read from rawDataSrc.
     * @param byteStride Size in bytes of a single elment of rawDataSrc.
     *                   note: byteStride must be in the range [1,8]
     */

    // bug: bisogna sistemare i numeri negativi.
    public static double[] toDoubleArray(byte[] rawDataSrc, int byteOffset, int len, int byteStride)
    {
        double[] result = new double[len];

        short[] shorts = toShortArray(rawDataSrc, byteOffset, len);

        for(int C=0;C<len;C++)
            result[C] = (double)shorts[C];

        /*
        // Zero-filling: create a temporary local copy of rawDataSrc of the correct size
        // already zero filled
        int C;
        byte[] src = new byte[len*byteStride];
        for(C = 0; C < len*byteStride && C+byteOffset < rawDataSrc.length; C++)
            src[C] = rawDataSrc[C+byteOffset];
        for( ; C < len*byteStride; C++ )
            src[C] = 0;

        // fill the result array
        long currentValue;
        for(C = 0; C < len; C++)
        {
            currentValue = 0;
            for(int B = 0; B < byteStride; B++)
                currentValue += (src[C*byteStride+B] << (8*B));
            result[C] = (double)currentValue;
        }
*/



        return (result);
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
