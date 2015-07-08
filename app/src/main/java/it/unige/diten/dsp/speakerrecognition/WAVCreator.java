package it.unige.diten.dsp.speakerrecognition;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class WAVCreator {
    private final static int RIFF_I = 0x46464952;
    private final static int WAVE_I = 0x45564157;
    private final static int FMT_I = 0x20746D66;
    private final static int DATA_I = 0x61746164;

    public byte[] data;
    public String fileName;

    private int chunkSize;
    private int subChunk1Size;
    private short audioFormat;
    private short numChannels;
    private int sampleRate;
    private int byteRate;
    private short blockAlign;
    private short bitsPerSample;
    private int subChunk2Size;

    /**
     * Instantiates an empty WAVE.
     */
    public WAVCreator() {
        this.data = null;
        this.fileName = null;

        this.chunkSize = 0;
        this.subChunk1Size = 0;
        this.audioFormat = 0;
        this.numChannels = 0;
        this.sampleRate = 0;
        this.byteRate = 0;
        this.blockAlign = 0;
        this.bitsPerSample = 0;
        this.subChunk2Size = 0;
    }

    /**
     * Create WAVE given fileName.
     *
     * @param fileName path to .wav file.
     */
    public WAVCreator(String fileName) {
        this();
        this.fileName = fileName;
    }

    /**
     * Create WAVE given data samples.
     *
     * @param fileName    path to .wav file.
     * @param data        samples array.
     * @param sampleRate  sample rate in Hertz.
     * @param numChannels numbers of channels.
     */
    public WAVCreator(String fileName, short[] data, int sampleRate, short numChannels) {
        this(fileName);

        this.data = toByteArray(data);
        this.sampleRate = sampleRate;
        this.numChannels = numChannels;

        // Because of short.
        this.bitsPerSample = 16;

        // Values for PCM Lossless.
        this.subChunk1Size = 16;
        this.audioFormat = 1;

        this.byteRate = sampleRate * numChannels * bitsPerSample / 8;
        this.blockAlign = (short) (numChannels * bitsPerSample / 8);
        this.subChunk2Size = data.length * bitsPerSample / 8;

        this.chunkSize = 4 + (8 + subChunk1Size) + (8 + subChunk2Size);
    }

    /**
     * Read WAVE file from filePath.
     *
     * @return true if read successful, false otherwise.
     */
    public boolean read() {
        boolean status = false;

        if (null != fileName) {
            try {
                // Variables initialization.
                DataInputStream inputStream = new DataInputStream(new FileInputStream(fileName));
                byte[] header = new byte[44];

                // Read first 44 bytes and store them into header.
                int readCount = inputStream.read(header);

                if (44 != readCount) {
                    throw new Exception("Unable to read header from WAVE file.");
                }

                // Fill information variables from header.
                status = extractHeaderInfo(header);

                data = new byte[subChunk2Size];

                // Read last "SubChunk2Size" of actual data.
                readCount = inputStream.read(data);

                if (subChunk2Size != readCount) {
                    throw new Exception("Unable to read data from WAVE file.");
                }

                inputStream.close();

            } catch (Exception e) {
                status = false;
            }
        }

        return status;
    }

    /**
     * Write WAVE file to filepath.
     *
     * @return true if write successful, false otherwise.
     */
    public boolean write() {
        boolean status = false;

        if (null != fileName) {
            status = true;

            try {
                // Variables initialization.
                DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(fileName));
                byte[] header = insertHeaderInfo();

                // Write first 44 bytes from header.
                outputStream.write(header);
                // Write data.
                outputStream.write(data);

                outputStream.close();
            } catch (Exception e) {
                status = false;
            }
        }

        return status;
    }

    /**
     * Convert short array into byte array for WAVE use.
     * Format: LSB-MSB
     *
     * @param src Constant short[] input.
     * @return byte[] array.
     */
    private byte[] toByteArray(final short[] src) {
        byte[] retV = new byte[src.length * 2];

        for (int i = 0; i < src.length; i++) {
            retV[2 * i] = (byte) ((src[i]) & 0xFF); // LSB
            retV[2 * i + 1] = (byte) ((src[i] >> 8) & 0xFF); // MSB
        }

        return retV;
    }

    /**
     * Converts byte array to integer in LITTLE ENDIAN mode.
     *
     * @param src    byte array to be converted.
     * @param offset 0-based offset in byte array.
     * @return LITTLE ENDIAN representation of array.
     */
    private int byteArraytoInteger(final byte[] src, int offset) {
        int retV = 0;

        retV |= ((src[offset++]) & 0x000000FF); // LSB
        retV |= ((src[offset++] << 8) & 0x0000FF00);
        retV |= ((src[offset++] << 16) & 0x00FF0000);
        retV |= ((src[offset] << 24) & 0xFF000000); // MSB

        return retV;
    }

    /**
     * Converts byte array to short in LITTLE ENDIAN mode.
     *
     * @param src    byte array to be converted.
     * @param offset 0-based offset in byte array.
     * @return LITTLE ENDIAN representation of array.
     */
    private short byteArrayToShort(final byte[] src, int offset) {
        short retV = 0;

        retV |= (short) ((src[offset++]) & 0x00FF); // LSB
        retV |= (short) ((src[offset] << 8) & 0xFF00); // MSB

        return retV;
    }

    /**
     * Converts integer to byte array in LITTLE ENDIAN mode.
     *
     * @param src integer to be converted.
     * @param dest output byte[] array.
     * @param offset 0-based offset in output byte array.
     */
    private void integerToByteArray(final int src, byte[] dest, int offset) {
        dest[offset++] = (byte) ((src) & 0xFF); // LSB
        dest[offset++] = (byte) ((src >> 8) & 0xFF);
        dest[offset++] = (byte) ((src >> 16) & 0xFF);
        dest[offset] = (byte) ((src >> 24) & 0xFF); // MSB
    }

    /**
     * Converts short to byte array in LITTLE ENDIAN mode.
     *
     * @param src short to be converted.
     * @param dest output byte[] array.
     * @param offset 0-based offset in output byte array.
     */
    private void shortToByteArray(final short src, byte[] dest, int offset) {
        dest[offset++] = (byte) ((src) & 0xFF); // LSB
        dest[offset] = (byte) ((src >> 8) & 0xFF); // MSB
    }

    /**
     * Check and assign values from byte array.
     *
     * @param data RAW data.
     * @return true if header was read correctly, false otherwise.
     */
    private boolean extractHeaderInfo(final byte[] data) {
        boolean status = false;

        if (data.length > 44) {
            // Field check.
            boolean riff_s = (RIFF_I == byteArraytoInteger(data, 0));
            boolean wave_s = (WAVE_I == byteArraytoInteger(data, 8));
            boolean fmt_s = (FMT_I == byteArraytoInteger(data, 12));
            boolean data_s = (DATA_I == byteArraytoInteger(data, 36));

            chunkSize = byteArraytoInteger(data, 4);
            subChunk1Size = byteArraytoInteger(data, 16);
            audioFormat = byteArrayToShort(data, 20);
            numChannels = byteArrayToShort(data, 22);
            sampleRate = byteArraytoInteger(data, 24);
            byteRate = byteArraytoInteger(data, 28);
            blockAlign = byteArrayToShort(data, 32);
            bitsPerSample = byteArrayToShort(data, 34);
            subChunk2Size = byteArraytoInteger(data, 40);

            status = (riff_s && wave_s && fmt_s && data_s);
        }

        return status;
    }

    /**
     * Create byte array to be written to file using values.
     *
     * @return RAW data.
     */
    private byte[] insertHeaderInfo() {
        byte[] retV = new byte[44];

        integerToByteArray(RIFF_I, retV, 0);
        integerToByteArray(chunkSize, retV, 4);

        integerToByteArray(WAVE_I, retV, 8);
        integerToByteArray(FMT_I, retV, 12);
        integerToByteArray(subChunk1Size, retV, 16);
        shortToByteArray(audioFormat, retV, 20);
        shortToByteArray(numChannels, retV, 22);
        integerToByteArray(sampleRate, retV, 24);
        integerToByteArray(byteRate, retV, 28);
        shortToByteArray(blockAlign, retV, 32);
        shortToByteArray(bitsPerSample, retV, 34);

        integerToByteArray(DATA_I, retV, 36);
        integerToByteArray(subChunk2Size, retV, 40);

        return retV;
    }
}
