package it.unige.diten.dsp.speakerrecognition.svmModeling;

import java.io.IOException;

import it.unige.diten.dsp.speakerrecognition.libsvm.*;

public abstract class TrainSVM {

    static DefaultSVMParameter defaultSVMParameter;
    static svm_parameter svmParameter;
    static svm_model svmModel;

    private static String filename;

    public static void setFilename(String filename) {
        if(filename != null)
        TrainSVM.filename = filename;
    }

    public static double C, G;

    public static void train(svm_problem svmProblem)
    {
        defaultSVMParameter = new DefaultSVMParameter(LoadFeatureFile.f);
        svmParameter = defaultSVMParameter.svmParameter;
        filename += ".model";

        // Change the parameters with the desired C and Gamma
        svmParameter.C = 0.125;
        svmParameter.gamma = 0.5;
        // Train the SVM
        svmModel = svm.svm_train(svmProblem, svmParameter);

        try {
            svm.svm_save_model(filename, svmModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
