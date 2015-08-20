package it.unige.diten.dsp.speakerrecognition.Structures;

public abstract class Keys
{
    // Keys of the feature extraction preferences
    public static String sampleRate         = "sample_rate";
    public static String frameDuration      = "frame_duration";
    public static String samplesInFrame     = "samples_in_frame";
    public static String overlapFactor      = "frame_overlap_factor";
    public static String frameSize          = "frame_size";
    public static String energyThreshold    = "energy_threshold";

    // Keys of the modeling preferences
    public static String cStart             = "c_start";
    public static String cEnd               = "c_end";
    public static String cStep              = "c_step";
    public static String gStart             = "g_start";
    public static String gEnd               = "g_end";
    public static String gStep              = "g_step";
    public static String folds              = "folds";
    public static String speakersName       = "speakers_name";
    public static String trainingFiles      = "training_files";
    public static String cCoefficients      = "c_coefficient_range";
    public static String gCoefficients      = "gamma_coefficient_range";
    public static String labelAssociation   = "label_association";
}
