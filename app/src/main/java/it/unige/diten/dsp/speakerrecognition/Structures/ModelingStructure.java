package it.unige.diten.dsp.speakerrecognition.Structures;

import java.io.File;

/**
 * Structure that contains all the variables modified in the "Modeling" preference screen
 */
public abstract class ModelingStructure {
    public static String[]  speakersNames;
    public static int[]     labels;
    public static File[]    trainingFiles;

    // Start, ending and step values
    public static int cStart, cEnd, cStep;
    public static int gStart, gStep, gEnd;

    // Folds of the cross validation
    public static int folds;
}
