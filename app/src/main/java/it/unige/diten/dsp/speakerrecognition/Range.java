package it.unige.diten.dsp.speakerrecognition;


public class Range {
    public double[] y_min;
    public double[] y_max;
    public Range()
    {
        y_min = new double[FeatureExtractor.MFCC_COUNT * 2];
        y_max = new double[FeatureExtractor.MFCC_COUNT * 2];
    }
}
