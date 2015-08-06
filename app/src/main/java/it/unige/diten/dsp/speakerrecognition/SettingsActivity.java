package it.unige.diten.dsp.speakerrecognition;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.mikephil.charting.utils.FileUtils;

import java.io.File;
import java.util.List;

import it.unige.diten.dsp.speakerrecognition.Structures.ModelingStructure;

import static it.unige.diten.dsp.speakerrecognition.R.string.modeling_key;
import static it.unige.diten.dsp.speakerrecognition.R.string.training_files_key;

/**
 * A {@link android.preference.PreferenceActivity} which implements and proxies the necessary calls
 * to be used with AppCompat.
 *
 * This technique can be used with an {@link android.app.Activity} class, not just
 * {@link android.preference.PreferenceActivity}.
 */
public class SettingsActivity extends PreferenceActivity {
    private AppCompatDelegate mDelegate;
    static Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        // Display the back button
        actionBar.setDisplayHomeAsUpEnabled(true);
/*
        String action = getIntent().getAction();

        if(action == null)
            addPreferencesFromResource(R.xml.preferences);
        else
        switch (action)
        {
            case ("it.unige.diten.dsp.speakerrecognition.FEATURE_EXTRACTION"):
                addPreferencesFromResource(R.xml.pref_extraction);
                break;

            case ("it.unige.diten.dsp.speakerrecognition.MODELING"):
                addPreferencesFromResource(R.xml.pref_modeling);
                break;
        }
*/
    }


    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        setContentView(R.layout.settings_layout);
        loadHeadersFromResource(R.xml.headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true; // TODO: replace with proper check
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }
    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }
    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }
    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }
    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }
    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }
    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }
    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }
    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class featureExtractionFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_extraction);
        }
    }

    public static class modelingFragment extends PreferenceFragment {
        private static String speakersNameKey;
        private static String trainingFilesKey;
        private static String cCoefficientsKey;

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
                coefficientsDialog.show(getFragmentManager(), null);
            }

            return true;
        }
    }
}