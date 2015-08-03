package it.unige.diten.dsp.speakerrecognition.svmModeling;

import java.io.IOException;

import it.unige.diten.dsp.speakerrecognition.libsvm.*;

public abstract class ScaleFeatureFile {

    /** Scale feature file with default x values
     *
     * @param filename path of the file to be scaled
     * @throws IOException
     */
    public static void Scale(String filename) throws IOException {

        String[] commandLine = new String[]{filename};

        svm_scale.main(commandLine);
    }

    /** Scale feature file specifying lower and upper bounds for x values
     *
     * @param filename  path of the file to be scaled
     * @param lower     desired lower bound for x values (feature values)
     * @param upper     desired lower bound for x values (feature values)
     * @throws IOException
     */
    public static void Scale(String filename, double lower, double upper) throws IOException {

        String l = String.valueOf(lower);
        String u = String.valueOf(upper);

        String[] commandLine = new String[]{"-l", l, "-u", u, filename};

        svm_scale.main(commandLine);
    }

    /** Scale feature file specifying lower and upper bounds for x and y values
     *
     * @param filename  path of the file to be scaled
     * @param lower     desired lower bound for x values (features values)
     * @param upper     desired lower bound for x values (features values)
     * @param y_lower   desired lower bound for y values (labels values)
     * @param y_upper   desired upper bound for y values (labels values)
     * @throws IOException
     */
    public static void Scale(String filename, double lower, double upper, double y_lower, double y_upper) throws IOException {
        String l        = String.valueOf(lower);
        String u        = String.valueOf(upper);
        String y_low    = String.valueOf(y_lower);
        String y_up     = String.valueOf(y_upper);

        String[] commandLine = new String[]{"-l", l, "-u", u, "-y", y_low, y_up, filename};

        svm_scale.main(commandLine);
    }

}
