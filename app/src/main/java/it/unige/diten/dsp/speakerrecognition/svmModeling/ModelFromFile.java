package it.unige.diten.dsp.speakerrecognition.SVMTraining;

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
        CrossValidation.setFilename(params[0]);

        // TODO: replace this with selected values
        CrossValidation.set_C_values(-5, 15, 1);
        CrossValidation.set_Gamma_values(3, -15, -1);

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
