package it.unige.diten.dsp.speakerrecognition;

import android.util.Log;


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
    public static int           FRAME_LENGTH_MS = 32;
    /// Expected sample rate in Hz
    public static int           SAMPLE_RATE = 8000;
    /// Number of samples in a single frame
    public static int           SAMPLES_IN_FRAME = SAMPLE_RATE * FRAME_LENGTH_MS / 1000;
    /// Frame overlap factor
    public static float         FRAME_OVERLAP_FACTOR = .75f;
    /// Number of Byte(s) per sample
    public final static int     BPS = 2;
    /// Frame size in Bytes
    public final static int     FRAME_BYTE_SIZE = SAMPLES_IN_FRAME * BPS;
    /// Distance in Bytes between the beginning of a frame and the following
    public final static int     FRAME_BYTE_SPACING = (int) ((float) FRAME_BYTE_SIZE * (1.0f - FRAME_OVERLAP_FACTOR));
    /// Distance in Shorts between the beginning of a frame and the following
    public final static int     FRAME_SHORT_SPACING = FRAME_BYTE_SPACING / 2;
    /// Generic energy threshold
    public final static double  ENERGY_THRESHOLD = 1E5;
    /// Container for all frames
    private static Frame[] frames = null;

    /// Read WAVE file from SDCard
    public static void readFromFile(String fileName) throws Exception
    {
        double[] hammingWindow = new double[SAMPLES_IN_FRAME];
        for( int C = 0; C < SAMPLES_IN_FRAME; C++)
            hammingWindow[C] = 0.54-0.46*Math.cos((2.0*Math.PI*(double)C)/((double)SAMPLES_IN_FRAME-1.0));

        int correctFrameCount;

        WAVCreator readWAV = new WAVCreator(fileName);
        readWAV.read();
        if (readWAV.getSampleRate() != SAMPLE_RATE)
            throw new Exception("Framer.readFromFile: Invalid sample rate!");

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
        FeatureExtractor.frameCount = frameCount;
        frames = new Frame[frameCount];
        correctFrameCount = frameCount;
        for (int C = 0; C < frameCount; C++)
        {
            frames[C] = new Frame();
            frames[C].data = new double[SAMPLES_IN_FRAME];

            // Copy samples to frame data (zero filling is included).
            for (int i = 0; (i < SAMPLES_IN_FRAME) && (C * FRAME_SHORT_SPACING + i < audioSamples.length); i++)
            {
                frames[C].data[i] = audioSamples[C * FRAME_SHORT_SPACING + i];
            }

            // Removal of low energy frames
            // if frame is invalid (i.e. has low energy), make it null

            // compute fourier transform
            frames[C].ft = new Complex[frames[C].data.length];
            for (int i = 0; i < frames[C].data.length; i++)
                frames[C].ft[i] = new Complex();
            DFT.computeDFT(frames[C].data, frames[C].ft);
            double energy = FCleaner.extractFreqEnergy(frames[C].ft, SAMPLE_RATE, 0, SAMPLE_RATE/2);
            TextWriter.appendText(MainActivity.PATH + "/" + "energyLog" + ".txt",String.valueOf(energy) + "\n");

            if(energy < ENERGY_THRESHOLD)
            {
                frames[C] = null;
                correctFrameCount--;
                FeatureExtractor.frameRemoved++;
            }

        }

        // Create a new frame array and fill it with non-null frames
        Frame[] frames2 = new Frame[correctFrameCount];
        int C = 0;
        for(int i = 0; i < frameCount; i++)
        {
            if(frames[i] != null)
                frames2[C++] = frames[i];
        }

        // Replace Framer.frames with the new array
        frames = frames2;

    }

    /// Returns frame array
    public static Frame[] getFrames()
    {
        return (frames);
    }
}
