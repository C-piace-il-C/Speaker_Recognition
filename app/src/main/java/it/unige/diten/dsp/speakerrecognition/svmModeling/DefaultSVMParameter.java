package it.unige.diten.dsp.speakerrecognition.svmModeling;

import it.unige.diten.dsp.speakerrecognition.libsvm.*;

/**
 * Provides a svm_parameter with default values already set
 */
public class DefaultSVMParameter {

    public svm_parameter svmParameter = new svm_parameter();

    public DefaultSVMParameter(double numFeature)
    {
        // Default parameters
        svmParameter.svm_type = 0;
        svmParameter.kernel_type = 2;
        svmParameter.degree = 3;
        svmParameter.gamma = 1/numFeature;
        svmParameter.coef0 = 0;

        // For training only
        svmParameter.cache_size = 100;
        svmParameter.eps = 0.001;
        svmParameter.C = 1;
        svmParameter.nu = 0.5;
        svmParameter.nr_weight = 0;
        svmParameter.weight_label = null;
        svmParameter.weight = null;
        svmParameter.p = 0.1;
        svmParameter.shrinking = 1;
        svmParameter.probability = 0;
    }
}
