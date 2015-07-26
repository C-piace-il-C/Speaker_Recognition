// CORREGGERE toDoubleArray E TESTARE

package it.unige.diten.dsp.speakerrecognition;

import android.nfc.Tag;
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
    /// Distance in Shorts between the beginning of a frame and the following
    public final static int FRAME_SHORT_SPACING = FRAME_BYTE_SPACING / 2;

    /// Container for all frames
    private static Frame[] frames = null;

    /// Read WAVE file from SDCard
    public static void readFromFile(String fileName) throws Exception
    {
        Log.v(TAG, "Called readFromFile: fileName = " + fileName);

        WAVCreator readWAV = new WAVCreator(fileName);
        readWAV.read();

        if (readWAV.getSampleRate() != SAMPLE_RATE)
        {
            Log.e(TAG, "[Sample rate] found: " + readWAV.getSampleRate() + ", expected: " + SAMPLE_RATE);
            throw new Exception("Framer.readFromFile: Invalid sample rate!");
        }

        // Clear old data (garbage collector)
        frames = null;

        short[] samples = readWAV.getSamples();

        // Remove eventual zeros at the beginning
        int offset = 0;
        while(samples[offset] == 0)
            offset++;

        short[] audioSamples = new short[samples.length - offset];
        for(int C = 0; C < samples.length - offset; C++)
        {
            audioSamples[C] = samples[C + offset];
        }
        int frameCount = audioSamples.length / FRAME_SHORT_SPACING;
        frames = new Frame[frameCount];

        for (int C = 0; C < frameCount; C++)
        {
            frames[C] = new Frame();
            frames[C].data = new double[SAMPLES_IN_FRAME];

            // Copy samples to frame data (zero filling is included).
            for (int i = 0; (i < SAMPLES_IN_FRAME) && (C * FRAME_SHORT_SPACING + i < audioSamples.length); i++)
            {
                frames[C].data[i] = audioSamples[C * FRAME_SHORT_SPACING + i];
            }
        }

        Log.v(TAG, "readFromFile: ended.");
    }

    /// Returns frame array
    public static Frame[] getFrames()
    {
        return (frames);
    }
}
