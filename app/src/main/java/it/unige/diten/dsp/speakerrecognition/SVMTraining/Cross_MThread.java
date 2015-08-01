package it.unige.diten.dsp.speakerrecognition.SVMTraining;


import it.unige.diten.dsp.speakerrecognition.libsvm.*;

/**
 * Created by doddo on 7/31/15.
 */
public class Cross_MThread implements Runnable {

    private int threadNum;
    private int threadCount;
    private svm_parameter svmParameter;
    private svm_problem svmProblem;
    private int cLength, gLength;
    private int[] log_C_coef, log_Gamma_coef;
    private double[][] results;
    private String[] parameters;

    public Cross_MThread(int threadNum, int threadCount, svm_problem svmProblem)
    {
        this.threadNum = threadNum;
        this.threadCount = threadCount;
        this.svmProblem = svmProblem;
        this.cLength = CrossValidation.cLength;
        this.gLength = CrossValidation.gLength;
        this.log_C_coef = CrossValidation.log_C_coef;
        this.log_Gamma_coef = CrossValidation.log_Gamma_coef;
        this.parameters = CrossValidation.parameters;
        this.results = CrossValidation.results;
        svmParameter = new DefaultSVMParameter(LoadFeatureFile.f).svmParameter;
    }

    public void run()
    {
        // Iterate the loop for every log2C and log2Gamma that the user wants to try
        for(int cParameters = threadNum; cParameters < cLength; cParameters += threadCount)
        {
            svmParameter.C = Math.pow(2, log_C_coef[cParameters]);
            for(int gParameters = 0; gParameters < gLength; gParameters ++)
            {
                svmParameter.gamma = Math.pow(2, log_Gamma_coef[gParameters]);
                // Do the cross validation and store the results
                // TODO: replace 5 with the user selected number of folds
                svm.svm_cross_validation
                        (svmProblem, svmParameter, 5, results[gLength * cParameters + gParameters]);
                // Store the parameters in the correct order of cross validation
                parameters[gLength * cParameters + gParameters] =
                        "log2c=" + log_C_coef[cParameters] +
                                ", log2g=" + log_Gamma_coef[gParameters] + ", ";
            }
        }
    }
}
