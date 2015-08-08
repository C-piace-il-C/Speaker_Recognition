package it.unige.diten.dsp.speakerrecognition.Fragments;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.NumberPicker;

import it.unige.diten.dsp.speakerrecognition.Dialogs.NumberPickerDialog;
import it.unige.diten.dsp.speakerrecognition.Dialogs.OverlapFactorDialog;
import it.unige.diten.dsp.speakerrecognition.R;
import it.unige.diten.dsp.speakerrecognition.Structures.FeatureExtractionStructure;

public class FeatureExtractionFragment extends PreferenceFragment {
    private String sampleRateKey;
    private String frameDurationKey;
    private String overlapFactorKey;

    private final String[] rates = new String[]
            {"8000", "11025", "16000", "22050", "32000", "44100", "48000"};

    private Preference overlapFactorPreference;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_extraction);

        sampleRateKey    = getString(R.string.sample_rate_key);
        frameDurationKey = getString(R.string.frame_duration_key);
        overlapFactorKey = getString(R.string.frame_overlap_factor_key);

        overlapFactorPreference = getPreferenceManager().findPreference(getString(R.string.frame_overlap_factor_key));

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = settings.edit();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        if(key.equals(sampleRateKey))
        {
            int maxRate = getValidSampleRates();
            ListPreference listPreference = (ListPreference) findPreference(sampleRateKey);
            String[] validRates = new String[maxRate];
            for(int i = 0; i < maxRate; i++)
            {
                validRates[i] = rates[i];
            }
            listPreference.setEntries(validRates);
            listPreference.setEntryValues(validRates);

            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    FeatureExtractionStructure.sampleRate = Integer.parseInt(newValue.toString());
                    preference.setSummary(String.valueOf(FeatureExtractionStructure.sampleRate));

                    Preference samplesInFramePreference =
                            getPreferenceManager()
                                    .findPreference(getString(R.string.samples_in_frame_key));

                    samplesInFramePreference.setSummary
                            (String.valueOf(FeatureExtractionStructure.sampleRate *
                                    FeatureExtractionStructure.frameDuration / 1000));

                    return true;
                }
            });
        }
        else if(key.equals(frameDurationKey))
        {
            DialogFragment dialogFragment =
                    NumberPickerDialog.newInstance(R.string.frame_duration, getPreferenceManager());
            super.onResume();
            dialogFragment.show(getFragmentManager(), "frame_duration");
        }
        else if(key.equals(overlapFactorKey))
        {
            OverlapFactorDialog overlapFactorDialog = new OverlapFactorDialog();
            super.onResume();
            overlapFactorDialog.show(getFragmentManager(), "Overlap_Factor");
        }
        return true;
    }

    private int getValidSampleRates() {
        // add the rates you wish to check against
        int[] rate = new int[]{8000, 11025, 16000, 22050, 32000, 44100, 48000};
        int i = 0, bufferSize;
        do {
            bufferSize = AudioRecord.getMinBufferSize(rate[i], AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
            i++;
        }
        while(bufferSize > 0 && i < rate.length);

        return (i);
    }


}
