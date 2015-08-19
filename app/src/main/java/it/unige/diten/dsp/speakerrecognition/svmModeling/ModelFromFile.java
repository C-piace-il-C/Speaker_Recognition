package it.unige.diten.dsp.speakerrecognition.svmModeling;

import android.os.AsyncTask;
import it.unige.diten.dsp.speakerrecognition.libsvm.*;

public class ModelFromFile extends AsyncTask <String, Void, Void> {

    static svm_problem svmProblem;

    protected void onPreExecute()
    {

    }

    @Override
    protected Void doInBackground(String... params) {

        /**
         * params[0] = name of the training file
         * params[1] = action [Cross, Train]
         */

        TrainSVM.filename = params[0];
        CrossValidation.filename = params[0];

        // TODO: replace this with selected values
        CrossValidation.cStart = -5;
        CrossValidation.cEnd = 15;
        CrossValidation.cStep = 1;

        CrossValidation.gStart = 3;
        CrossValidation.gEnd = -15;
        CrossValidation.gStep = -1;

        try {
            svmProblem = LoadFeatureFile.load(params[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }


        switch(params[1])
        {
            case("Cross"):
                CrossValidation.cross_validate(svmProblem);
                break;

            case("Train"):
                TrainSVM.train(svmProblem);
                break;
        }

        return null;
    }
}
