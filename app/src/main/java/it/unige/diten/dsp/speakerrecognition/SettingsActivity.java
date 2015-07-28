package it.unige.diten.dsp.speakerrecognition;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by doddo on 7/28/15.
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    public static class SettingsFragment extends PreferenceFragment {

        CheckBoxPreference dftBox;
        CheckBoxPreference fftBox;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            dftBox = (CheckBoxPreference) findPreference("dftBox");

            dftBox.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                        dftBox.setChecked(true);
                        fftBox.setChecked(false);
                        MainActivity.transformType = TransformSelector.TT_DFT;
                        return true;

                }
            });

            fftBox = (CheckBoxPreference) findPreference("fftBox");

            fftBox.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                        fftBox.setChecked(true);
                        dftBox.setChecked(false);
                        MainActivity.transformType = TransformSelector.TT_FFT;
                        return true;

                }
            });
        }




        @Override
        public void onDestroy() {
            super.onDestroy();
        }
    }

}
