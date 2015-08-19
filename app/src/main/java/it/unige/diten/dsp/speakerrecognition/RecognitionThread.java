package it.unige.diten.dsp.speakerrecognition;

import it.unige.diten.dsp.speakerrecognition.libsvm.*;

public class RecognitionThread implements Runnable
{
    private int threadNumber;
    private int threadCount;
    private svm_node[][][] featuresPtr;
    private int[][] resultPtr;
    private svm_model[] models;

    public RecognitionThread(int tn, int tc, svm_node[][][] featuresPtr, int[][] resultsPtr, svm_model[] models)
    {
        this.threadNumber   = tn;
        this.threadCount    = tc;
        this.featuresPtr    = featuresPtr;
        this.resultPtr      = resultsPtr;
        this.models         = models;
    }

    public void run()
    {
        int frameCount = FeatureExtractor.MFCC.length;

        // Multithread: each thread works on different values of C.
        // Eventually, all values of C (from 0 to frameCount-1) are covered.
        for(int C = threadNumber; C < frameCount; C += threadCount)
        {
            for(int M = 0; M < MySVM_Async.modelCnt; M++)
            {
                resultPtr[M][(int)svm.svm_predict(models[M], featuresPtr[M][C])]++;
            }

        }
    }
}
