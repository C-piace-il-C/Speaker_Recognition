package it.unige.diten.dsp.speakerrecognition;

import it.unige.diten.dsp.speakerrecognition.libsvm.*;

public class RecognitionThread implements Runnable
{
    private int threadNumber;
    private int threadCount;
    private svm_node[][] featuresPtr;
    private int[] resultPtr;
    private svm_model model;
    public RecognitionThread(int tn, int tc, svm_node[][] featuresPtr, int[] resultsPtr, svm_model model)
    {
        this.threadNumber   = tn;
        this.threadCount    = tc;
        this.featuresPtr    = featuresPtr;
        this.resultPtr      = resultsPtr;
        this.model          = model;
    }
    public void run()
    {
        int frameCount = FeatureExtractor.MFCC.length;
        for(int C = threadNumber; C < frameCount; C += threadCount)
        {
            resultPtr[(int)svm.svm_predict(model, featuresPtr[C])] ++;
        }
    }
}
