// CORREGGERE toDoubleArray E TESTARE

package it.unige.diten.dsp.speakerrecognition;

import android.util.Log;

import it.unige.diten.dsp.speakerrecognition.WavIO.WavIO;

/**
 * Framer
 * E' una classe astratta che si occupa di leggere un file .WAV per poi suddividerlo in frames
 * secondo i parametri noti (public final static...)
 * Il codice dovrebbe adattarsi a tutti parametri (compreso BPS a partire dal commit corrente)
 */
public abstract class Framer
{
    public final static String TAG = "Framer";

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
        // byteStride must be even.
        if ((byteStride & 0x1) != 0)
            return null;

        double[] retV = new double[len];

        int i;

        // Convert input to double.
        for (i = 0; (i < len) && (byteOffset + i * byteStride + 1 < src.length); i++)
        {
            retV[i] =  (short) ((src[byteOffset + i * byteStride])          & 0x00FF); //LSB
            retV[i] += (short) ((src[byteOffset + i * byteStride + 1] << 8) & 0xFF00); //MSB
        }

        // Zero-filling.
        while (i < len)
            retV[i++] = .0;

        return (retV);
    }

    /// Read WAVE file from SDCard
    public static void readFromFile(String fileName) throws Exception
    {
        //Log.v (TAG, "Called readFromFile.");
        WavIO readWAV = new WavIO(fileName);
        readWAV.read();

        if (readWAV.getSampleRate() != SAMPLE_RATE)
        {
            Log.e(TAG, "[Sample rate] found: " + readWAV.getSampleRate() + ", expected: " + SAMPLE_RATE);
            throw new Exception("Framer.readFromFile: Invalid sample rate!");
        }
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

        //Log.v(TAG, "readFromFile: ended.");
    }

    /// Returns frame array
    public static Frame[] getFrames()
    {
        return (frames);
    }
}
