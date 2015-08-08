package it.unige.diten.dsp.speakerrecognition.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import java.io.File;

import it.unige.diten.dsp.speakerrecognition.Dialogs.CoefficientsDialog;
import it.unige.diten.dsp.speakerrecognition.Dialogs.FileChooserDialog;
import it.unige.diten.dsp.speakerrecognition.Dialogs.FoldsPickerDialog;
import it.unige.diten.dsp.speakerrecognition.R;
import it.unige.diten.dsp.speakerrecognition.Structures.ModelingStructure;

public class ModelingFragments extends PreferenceFragment {
    private static String speakersNameKey;
    private static String trainingFilesKey;
    private static String cCoefficientsKey;
    private static String gCoefficientsKey;
    private static String foldsKey;

    private static Preference labelsPreference;
    private static Preference speakersNamePreference;


    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_modeling);
        speakersNameKey     = getString(R.string.speakers_name_key);
        trainingFilesKey    = getString(R.string.training_files_key);
        cCoefficientsKey    = getString(R.string.c_coefficient_range_key);
        gCoefficientsKey    = getString(R.string.gamma_coefficient_range_key);
        foldsKey            = getString(R.string.folds_key);

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = settings.edit();


        labelsPreference = getPreferenceManager().findPreference(getString(R.string.labels_association_key));
        speakersNamePreference = getPreferenceManager().findPreference(getString(R.string.speakers_name_key));

        speakersNamePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(newValue.toString().equals(""))
                    return false;

                String[] names = newValue.toString().split(",");
                ModelingStructure.speakersNames = names;

                int N = names.length;

                ModelingStructure.labels = new int[N];
                String labelsSummary = "";

                for(int i = 0; i < N; i++)
                {
                    ModelingStructure.labels[i] = i;
                    labelsSummary += names[i] + ": " + i + ", ";
                }

                labelsSummary = labelsSummary.substring(0, labelsSummary.length() - 2);
                labelsPreference.setSummary(labelsSummary);
                editor.putString(labelsPreference.getKey(), labelsSummary);
                editor.apply();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO: clean this sentence
        labelsPreference.setSummary(settings.getString(getString(R.string.labels_association_key), ""));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        String key = preference.getKey();
        if(key.equals(trainingFilesKey)) {
            new FileChooserDialog(getActivity()).setFileListener(new FileChooserDialog.FileSelectedListener() {
                @Override
                public void fileSelected(final File[] files) {
                    if (files != null)
                        ModelingStructure.trainingFiles = files;
                }
            }).showDialog();
        }
        else if(key.equals(cCoefficientsKey))
        {
            CoefficientsDialog coefficientsDialog = new CoefficientsDialog();
            super.onResume();
            coefficientsDialog.show(getFragmentManager(), "C_Coefficient");
        }
        else if(key.equals(gCoefficientsKey))
        {
            CoefficientsDialog coefficientsDialog = new CoefficientsDialog();
            super.onResume();
            coefficientsDialog.show(getFragmentManager(), "Gamma_Coefficient");
        }
        else if(key.equals(foldsKey))
        {
            FoldsPickerDialog foldsPickerDialog = new FoldsPickerDialog();
            super.onResume();
            foldsPickerDialog.show(getFragmentManager(), "Folds");
        }
        return true;
    }
}
