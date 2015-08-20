package it.unige.diten.dsp.speakerrecognition.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import it.unige.diten.dsp.speakerrecognition.Framer;
import it.unige.diten.dsp.speakerrecognition.R;
import it.unige.diten.dsp.speakerrecognition.Structures.FeatureExtractionStructure;
import it.unige.diten.dsp.speakerrecognition.Structures.ModelingStructure;

public class OverlapFactorDialog extends DialogFragment {

    private Activity            activity;
    private AlertDialog.Builder builder;
    private AlertDialog         alertDialog;
    private NumberPicker        numberPicker;
    private MyView              myView;

    private final int   N = 10;
    private final int   minValue = 0;

    private PreferenceManager preferenceManager;
    private Preference preference;

    private int         maxValue;
    private String      overlapFactorKey;
    private String[]    decimals;

    private SharedPreferences settings;

    public static OverlapFactorDialog newInstance(int title)
    {
        OverlapFactorDialog fragment = new OverlapFactorDialog();

        Bundle args = new Bundle();
        args.putInt("title", title);
        fragment.setArguments(args);

        return fragment;
    }

    public static OverlapFactorDialog newInstance(PreferenceManager preferenceManager)
    {
        OverlapFactorDialog fragment = new OverlapFactorDialog();

        fragment.preferenceManager = preferenceManager;

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        activity = getActivity();

        builder     = new AlertDialog.Builder(activity);
        myView      = new MyView(activity);
        settings    = PreferenceManager.getDefaultSharedPreferences(activity);

        decimals = new String[N * N];
        for(int i = 0; i < N; i++)
            for(int j = 0; j < N; j++)
            {
                decimals[N * i + j] = i + "" + j;
            }
        maxValue = decimals.length - 1;

        overlapFactorKey = getString(R.string.frame_overlap_factor_key);
        preference       = preferenceManager.findPreference(overlapFactorKey);

        setPickers();
        setListeners();

        return alertDialog;

    }

    private void setListeners()
    {
        alertDialog = builder
                .setView(myView)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Back", null)
                .setTitle("Overlap factor")
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = numberPicker.getValue();
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt(overlapFactorKey, position);
                        editor.apply();

                        String value = "0." + decimals[position];
                        preference.setSummary(value);
                        FeatureExtractionStructure.overlapFactor = Float.parseFloat(value);
                        Framer.FRAME_OVERLAP_FACTOR = FeatureExtractionStructure.overlapFactor;

                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private void setPickers()
    {
        numberPicker = (NumberPicker) myView.findViewById(R.id.overlapPicker);
        numberPicker.setDisplayedValues(decimals);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setMinValue(minValue);
        numberPicker.setValue(settings.getInt(overlapFactorKey, 75));
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
            View view = inflate(getContext(), R.layout.overlap_dialog, null);
            addView(view);
        }
    }
}
