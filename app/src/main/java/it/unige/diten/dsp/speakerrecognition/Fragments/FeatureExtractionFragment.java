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

import it.unige.diten.dsp.speakerrecognition.Dialogs.NumberPickerDialog;
import it.unige.diten.dsp.speakerrecognition.Dialogs.OverlapFactorDialog;
import it.unige.diten.dsp.speakerrecognition.R;
import it.unige.diten.dsp.speakerrecognition.Structures.FeatureExtractionStructure;

public class FeatureExtractionFragment extends PreferenceFragment {
    private String sampleRateKey;
    private String frameDurationKey;
    private String overlapFactorKey;
    private String samplesInFrameKey;
    private String frameSizeKey;

    private final String[] rates = new String[]
            {"8000", "11025", "16000", "22050", "32000", "44100", "48000"};


    private PreferenceManager preferenceManager;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_extraction);

        preferenceManager = getPreferenceManager();

        sampleRateKey    = getString(R.string.sample_rate_key);
        frameDurationKey = getString(R.string.frame_duration_key);
        samplesInFrameKey= getString(R.string.samples_in_frame_key);
        overlapFactorKey = getString(R.string.frame_overlap_factor_key);
        frameSizeKey     = getString(R.string.frame_size_key);

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = settings.edit();

        initSummaries();
        initRates();

    }

    private void initRates()
    {
        int maxRate = getValidSampleRates();
        ListPreference listPreference = (ListPreference) findPreference(sampleRateKey);
        String[] validRates = new String[maxRate];
        System.arraycopy(rates, 0, validRates, 0, maxRate);
        listPreference.setEntries(validRates);
        listPreference.setEntryValues(validRates);
    }


    private void initSummaries()
    {
        // Preferences that have a summary that needs to be updated
        Preference frameDuration;
        Preference sampleRate;
        Preference samplesInFrame;
        Preference overlapFactor;
        Preference frameSize;
        // TODO: join this two pieces
        frameDuration           = preferenceManager.findPreference(frameDurationKey);
        sampleRate              = preferenceManager.findPreference(sampleRateKey);
        samplesInFrame          = preferenceManager.findPreference(samplesInFrameKey);
        overlapFactor           = preferenceManager.findPreference(overlapFactorKey);
        frameSize               = preferenceManager.findPreference(frameSizeKey);

        frameDuration .setSummary("" + settings.getInt(frameDurationKey, 32));
        sampleRate    .setSummary("" + settings.getString(sampleRateKey, "8000"));
        samplesInFrame.setSummary("" + settings.getInt(samplesInFrameKey, 256));
        overlapFactor .setSummary("0." + settings.getInt(overlapFactorKey, 75));
        frameSize     .setSummary("" + settings.getInt(frameSizeKey, 512));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        if(key.equals(sampleRateKey))
        {
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int value = Integer.parseInt(newValue.toString());

                    FeatureExtractionStructure.sampleRate = value;
                    preference.setSummary("" + value);

                    Preference samplesInFramePreference =
                            getPreferenceManager()
                                    .findPreference(getString(R.string.samples_in_frame_key));

                    samplesInFramePreference.setSummary
                            (
                               "" + (value *
                                       FeatureExtractionStructure.frameDuration / 1000));

                    editor.putString(sampleRateKey, "" + value);
                    editor.putInt(samplesInFrameKey,
                            (value *
                                    FeatureExtractionStructure.frameDuration / 1000));
                    editor.apply();

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
            OverlapFactorDialog overlapFactorDialog =
                    OverlapFactorDialog.newInstance(getPreferenceManager());
            super.onResume();
            overlapFactorDialog.show(getFragmentManager(), "overlap_factor");
        }
        return true;
    }

    private int getValidSampleRates() {
        // add the rates you wish to check against
        int[] rates = new int[]{8000, 11025, 16000, 22050, 32000, 44100, 48000};
        int i = 0, bufferSize;

        do {
            bufferSize = AudioRecord.getMinBufferSize(rates[i], AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
            i++;
        }
        while(bufferSize > 0 && i < rates.length);

        // If i < rates.length his value is an index of an invalid value in the rates array
        return (i < rates.length ? i - 1 : i);
    }


}
