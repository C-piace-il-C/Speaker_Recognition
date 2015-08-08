package it.unige.diten.dsp.speakerrecognition.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import it.unige.diten.dsp.speakerrecognition.Fragments.FeatureExtractionFragment;
import it.unige.diten.dsp.speakerrecognition.R;
import it.unige.diten.dsp.speakerrecognition.SettingsActivity;
import it.unige.diten.dsp.speakerrecognition.Structures.FeatureExtractionStructure;
import it.unige.diten.dsp.speakerrecognition.Structures.ModelingStructure;

public class NumberPickerDialog extends DialogFragment{

    private Activity            activity;
    private AlertDialog.Builder builder;
    private AlertDialog         alertDialog;
    private NumberPicker        numberPicker;
    private MyView              myView;

    private int minValue;
    private int maxValue;

    private PreferenceManager preferenceManager = null;
    private Preference[] preferences;

    private final String frameTitle = "Frame Duration";
    private final String frameTag   = "frame_duration";
    private final String foldsTitle = "Folds";
    private final String foldsTag   = "folds";


    private String foldsKey;
    private String frameDurationKey;

    private SharedPreferences settings;

    public static NumberPickerDialog newInstance(int title)
    {
        NumberPickerDialog fragment = new NumberPickerDialog();

        Bundle args = new Bundle();
        args.putInt("title", title);
        fragment.setArguments(args);

        return fragment;
    }

    public static NumberPickerDialog newInstance(int title, PreferenceManager preferenceManager)
    {
        NumberPickerDialog fragment = new NumberPickerDialog();

        fragment.preferenceManager = preferenceManager;

        Bundle args = new Bundle();
        args.putInt("title", title);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String tag = getTag();
        this.activity = getActivity();

        builder = new AlertDialog.Builder(activity);
        myView  = new MyView(activity);
        settings    = PreferenceManager.getDefaultSharedPreferences(activity);

        switch (tag)
        {
            case(frameTag):
                minValue = 25;
                maxValue = 45;
                frameDurationKey = getString(R.string.frame_duration_key);
                preferences = new Preference[2];
                preferences[0] =
                        preferenceManager.findPreference(frameDurationKey);
                preferences[1] =
                        preferenceManager.findPreference(getString(R.string.samples_in_frame_key));
                setPickers(frameDurationKey, 32);
                setListeners(frameTitle, frameTag);
                break;
            case(foldsTag):
                minValue = 2;
                maxValue = 10;
                foldsKey = getString(R.string.folds_key);
                setPickers(foldsKey, 2);
                setListeners(foldsTitle, foldsTag);
                break;
        }


        return alertDialog;
    }

    private void setListeners(String title, final String tag)
    {
        alertDialog = builder
                .setView(myView)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Back", null)
                .setTitle(title)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        savePreferences(tag, numberPicker.getValue());
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private void savePreferences(String tag, int value)
    {
        switch(tag)
        {
            // Bug in Android Studio? If I remove the brackets it says that editor is already
            // initialized in this scope in the second case
            case(frameTag): {
                FeatureExtractionStructure.frameDuration = value;
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(frameDurationKey, value);
                editor.apply();
                preferences[0].setSummary(String.valueOf(value));
                preferences[1].setSummary(
                        String.valueOf(
                                FeatureExtractionStructure.sampleRate * value / 1000
                        )
                );
                break;
            }
            case(foldsTag):
                ModelingStructure.folds = numberPicker.getValue();
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(foldsKey, ModelingStructure.folds);
                editor.apply();
                break;
        }
    }

    private void setPickers(String key, int def)
    {
        numberPicker = (NumberPicker) myView.findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setMinValue(minValue);
        numberPicker.setValue(settings.getInt(key, def));
    }

    private void showDialog()
    {
        alertDialog.show();
    }

    private class MyView extends FrameLayout
    {
        public MyView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            initView();
        }

        public MyView(Context context, AttributeSet attrs) {
            super(context, attrs);
            initView();
        }

        public MyView(Context context) {
            super(context);
            initView();
        }

        private void initView() {
            View view = inflate(getContext(), R.layout.number_picker, null);
            addView(view);
        }
    }
}
