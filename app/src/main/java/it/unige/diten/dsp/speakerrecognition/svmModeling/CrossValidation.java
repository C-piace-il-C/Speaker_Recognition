package it.unige.diten.dsp.speakerrecognition.svmModeling;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import it.unige.diten.dsp.speakerrecognition.libsvm.*;

public abstract class CrossValidation {

    public static double[][]   results;
    public static double[]     percentages;
    public static int[] exactResults;
    // TODO the user must not enter invalid values for those six variables
    public static int cStart, cEnd, cStep;
    public static int gStart, gEnd, gStep;
    public static String filename;

    public static int cLength;
    public static int gLength;
    public static int[] log_C_coef;
    public static int[] log_Gamma_coef;
    public static String[] parameters;

    static FileOutputStream fileOutputStream;
    static BufferedOutputStream bufferedOutputStream;


    public static void cross_validate(svm_problem svmProblem) {

        // Number of step = [(End - Start) / Step] + 1
        // Valid only if the Step is adequate for starting at Start and arriving exactly at End
        // Calculated when the user selects c and g
        cLength = ((cEnd - cStart) / cStep) +1;
        gLength = ((gEnd - gStart) / gStep) +1;

        // Containers for the desired coefficients
        log_C_coef = new int[cLength];
        log_Gamma_coef = new int[gLength];

        initCoefficientsArrays();

        // Array that stores each line of the file to be written
        parameters = new String[log_C_coef.length * log_Gamma_coef.length];

        // M x N matrix that stores l results (l == rows of the model == labels) for each
        // combination of C and Gamma
        results = new double[cLength * gLength][LoadFeatureFile.l];

        // TODO: replace numCores with actual cores on the device
        int numCores = 8;

        try {
            Thread[] threads = new Thread[numCores];
            for (int C = 0; C < numCores; C++) {
                threads[C] = new Thread(new Cross_MThread(C, numCores, svmProblem));
                threads[C].start();
            }
            for (int C = 0; C < numCores; C++)
                threads[C].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        calculatePercentages(svmProblem);
        writeResultsToFile();
    }

    private static void initCoefficientsArrays()
    {
        if(cLength == gLength)
        {
            for(int i = 0; i < cLength; i++)
            {
                log_C_coef[i]       = cStart + (cStep * i);
                log_Gamma_coef[i]   = gStart + (gStep * i);
            }
        }
        else if(cLength > gLength)
        {
            int i;
            for(i = 0; i < gLength; i++)
            {
                log_C_coef[i]       = cStart + (cStep * i);
                log_Gamma_coef[i]   = gStart + (gStep * i);
            }
            for(; i < cLength; i++)
                log_C_coef[i]       = cStart + (cStep * i);
        }
        else
        {
            int i;
            for(i = 0; i < cLength; i++)
            {
                log_C_coef[i]       = cStart + (cStep * i);
                log_Gamma_coef[i]   = gStart + (gStep * i);
            }
            for(; i < gLength; i++)
                log_Gamma_coef[i]   = gStart + (gStep * i);
        }
    }

    private static void calculatePercentages(svm_problem svmProblem)
    {
        exactResults = new int[results.length];
        percentages = new double[results.length];

        // For every combination of C and G
        for(int i = 0; i < cLength * gLength; i++)
        {
            // For every result obtained
            for(int j = 0; j < LoadFeatureFile.l; j++)
            {
                if(results[i][j] == svmProblem.y[j])
                    exactResults[i]++;
            }
            percentages[i] = (double) exactResults[i] / LoadFeatureFile.l;
            parameters[i] += "rate = " + (percentages[i] * 100);
        }
    }

    private static void writeResultsToFile()
    {
        filename += ".coeff";

        try {
            fileOutputStream = new FileOutputStream(filename);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            for (String line : parameters) {
                bufferedOutputStream.write((line + "\n").getBytes());
            }

            bufferedOutputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
