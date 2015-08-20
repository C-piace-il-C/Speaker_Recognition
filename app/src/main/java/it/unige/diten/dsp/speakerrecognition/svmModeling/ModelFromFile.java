package it.unige.diten.dsp.speakerrecognition.svmModeling;

import android.os.AsyncTask;

import it.unige.diten.dsp.speakerrecognition.Structures.ModelingStructure;
import it.unige.diten.dsp.speakerrecognition.libsvm.*;
import it.unige.diten.dsp.speakerrecognition.svmModeling.CrossValidation;
import it.unige.diten.dsp.speakerrecognition.svmModeling.LoadFeatureFile;
import it.unige.diten.dsp.speakerrecognition.svmModeling.TrainSVM;

public class ModelFromFile extends AsyncTask <String, Void, Void> {

    static svm_problem svmProblem;

    protected void onPreExecute()
    {

    }

    @Override
    public Void doInBackground(String... params) {

        /**
         * params[0] = name of the training file
         * params[1] = action [Cross, Train]
         */

        TrainSVM.setFilename(params[0]);
        CrossValidation.setFilename(params[0]);

        // TODO: replace this with selected values
        CrossValidation.set_C_values(
                ModelingStructure.cStart, ModelingStructure.cEnd, ModelingStructure.cStep);
        CrossValidation.set_Gamma_values(
                ModelingStructure.gStart, ModelingStructure.gEnd, ModelingStructure.gStep);

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
