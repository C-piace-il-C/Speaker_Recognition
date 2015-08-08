package it.unige.diten.dsp.speakerrecognition.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import it.unige.diten.dsp.speakerrecognition.Dialogs.OverlapFactorDialog;
import it.unige.diten.dsp.speakerrecognition.R;

public class FeatureExtractionFragment extends PreferenceFragment {
    private String overlapFactorKey;

    private Preference overlapFactorPreference;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_extraction);

        overlapFactorKey = getString(R.string.frame_overlap_factor_key);

        overlapFactorPreference = getPreferenceManager().findPreference(getString(R.string.frame_overlap_factor_key));

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = settings.edit();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if(preference.getKey().equals(overlapFactorKey))
        {
            OverlapFactorDialog overlapFactorDialog = new OverlapFactorDialog();
            super.onResume();
            overlapFactorDialog.show(getFragmentManager(), "Overlap_Factor");
        }
        return true;
    }
}
