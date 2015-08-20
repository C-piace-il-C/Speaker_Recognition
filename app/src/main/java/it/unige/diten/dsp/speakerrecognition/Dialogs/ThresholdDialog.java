package it.unige.diten.dsp.speakerrecognition.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import it.unige.diten.dsp.speakerrecognition.Framer;
import it.unige.diten.dsp.speakerrecognition.R;
import it.unige.diten.dsp.speakerrecognition.Structures.FeatureExtractionStructure;

public class ThresholdDialog extends DialogFragment{

    // TODO: write a structure for this five variables: they are in five different classes
    private Activity activity;
    private AlertDialog.Builder builder;
    private AlertDialog         alertDialog;
    private NumberPicker        numberPicker;
    private MyView              myView;
    private EditText            editText;

    private final String exponent   = "exponent";
    private final String base       = "base";
    private final String title      = "Energy threshold";
    private final int minValue      = 0;
    private final int maxValue      = 10;

    private PreferenceManager preferenceManager = null;

    private SharedPreferences settings;

    public static ThresholdDialog newInstance(int title)
    {
        ThresholdDialog fragment = new ThresholdDialog();

        Bundle args = new Bundle();
        args.putInt("title", title);
        fragment.setArguments(args);

        return fragment;
    }

    public static ThresholdDialog newInstance(PreferenceManager preferenceManager)
    {
        ThresholdDialog fragment = new ThresholdDialog();

        fragment.preferenceManager = preferenceManager;

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.activity = getActivity();
        final String tag = getTag();

        builder = new AlertDialog.Builder(activity);
        myView  = new MyView(activity);
        settings    = PreferenceManager.getDefaultSharedPreferences(activity);

        setListeners(tag);
        setViews();

        return alertDialog;
    }

    private void setListeners(final String tag)
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
                final SharedPreferences.Editor editor = settings.edit();

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the base in the editText
                        double value = Double.valueOf(editText.getText().toString());
                        // Save the number for future dialog openings
                        editor.putString(base, value + "");
                        // Get the exponent in the numberPicker
                        int exp = numberPicker.getValue();
                        // Save the number for future dialog openings
                        editor.putInt(exponent, exp);
                        // Calculate the threshold selected
                        value *= Math.pow(10, exp);
                        // Save it in the places that needs it
                        FeatureExtractionStructure.energyThreshold = value;
                        Framer.ENERGY_THRESHOLD = value;
                        // Save it
                        editor.putString(tag, value + "").apply();
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private void setViews()
    {
        editText = (EditText) myView.findViewById(R.id.thresholdText);
        numberPicker = (NumberPicker) myView.findViewById(R.id.thresholdPicker);

        numberPicker.setMaxValue(maxValue);
        numberPicker.setMinValue(minValue);
        numberPicker.setValue(settings.getInt(exponent, 7));

        editText.setText(settings.getString(base, "5"));
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
            View view = inflate(getContext(), R.layout.energy_threshold_dialog, null);
            addView(view);
        }
    }
}
