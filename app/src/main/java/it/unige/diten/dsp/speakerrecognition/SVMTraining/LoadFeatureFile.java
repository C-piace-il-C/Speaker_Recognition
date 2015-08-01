package it.unige.diten.dsp.speakerrecognition.SVMTraining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import it.unige.diten.dsp.speakerrecognition.libsvm.*;

/**
 * Created by doddo on 7/31/15.
 */
/* TODO: modify the class in order to accept an array of files, label them and join them in one single file */

public abstract class LoadFeatureFile {

    // l = rows of the features file, f = number of features
    public static int l = 0, f = 0;
    // container for the feature file
    static svm_problem svmProblem = new svm_problem();

    public static svm_problem load(String filename) throws Exception
    {
        File file = new File(filename);
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

        String line = bufferedReader.readLine();

        String features = line.substring(0, line.lastIndexOf(':'));
        features = features.substring(features.lastIndexOf(' ') + 1);
        f = Integer.valueOf(features);

        for (; ; ) {
            if (line != null) {
                l++;
                line = bufferedReader.readLine();
            } else {
                break;
            }
        }

        fileInputStream.getChannel().position(0);
        bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));


        // number of rows of features
        svmProblem.l = l;
        // labels for each row
        svmProblem.y = new double[l];
        // svm_node for indexes and values
        svmProblem.x = new svm_node[l][f];

        // Initialize svm_nodes in SvmProblem.x
        for (int i = 0; i < svmProblem.x.length; i++) {
            for (int j = 0; j < svmProblem.x[0].length; j++) {
                svmProblem.x[i][j] = new svm_node();
            }
        }

        populateArray(bufferedReader);
        bufferedReader.close();

        return svmProblem;
    }

    private static void populateArray(BufferedReader bufferedReader) {
        try {
            for (int C = 0; C < l; C++) {
                int i;
                int pos;
                String line = bufferedReader.readLine();

                if (line.charAt(0) == '+' || line.charAt(0) == '-') {
                    svmProblem.y[C] = (Double.valueOf(line.substring(0, 2)));
                } else {
                    svmProblem.y[C] = Double.valueOf(line.substring(0, 1));
                }

                int index = 1;
                for (i = 0; i < f; i++) {
                    pos = line.indexOf(':');

                    line = line.substring(pos + 1);
                    svmProblem.x[C][i].index = i + 1;
                    svmProblem.x[C][i].value = Double.valueOf(line.substring(0, line.indexOf(' ')));
                    index++;
                }
                //svmProblem.x[C][i].index = -1;
                //svmProblem.x[C][i].value = .0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
