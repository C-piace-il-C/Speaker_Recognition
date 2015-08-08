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
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import it.unige.diten.dsp.speakerrecognition.R;
import it.unige.diten.dsp.speakerrecognition.Structures.ModelingStructure;

public class CoefficientsDialog extends DialogFragment{

    private Activity activity;
    private MyView myView;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private NumberPicker startPicker, endPicker, stepPicker;

    final private String C      = "C_Coefficient";
    final private String Gamma  = "Gamma_Coefficient";

    private String cStartKey;
    private String cEndKey;
    private String cStepKey;
    private String gStartKey;
    private String gEndKey;
    private String gStepKey;

    String[] defaultValues = new String[] {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
            "12", "13", "14", "15", "-15", "-14", "-13", "-12", "-11",
            "-10", "-9", "-8","-7", "-6", "-5", "-4", "-3", "-2", "-1"
    };

    final int minValue = 0;
    final int maxValue = defaultValues.length - 1;

    private SharedPreferences settings;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String tag = getTag();

        cStartKey   = getString(R.string.c_start_key);
        cEndKey     = getString(R.string.c_end_key);
        cStepKey    = getString(R.string.c_step_key);
        gStartKey   = getString(R.string.g_start_key);
        gEndKey     = getString(R.string.g_end_key);
        gStepKey    = getString(R.string.g_step_key);

        activity    = getActivity();
        settings    = PreferenceManager.getDefaultSharedPreferences(activity);
        builder     = new AlertDialog.Builder(activity);
        myView      = new MyView(activity);

        setPickers(tag);

        switch(tag)
        {
            case (C):
                String cTitle = "C coefficient range";
                init(cTitle, tag);
                break;
            case (Gamma):
                String gTitle = "Gamma coefficient range";
                init(gTitle, tag);
                break;
            default:
                dismiss();
                break;
        }

        return alertDialog;
    }

    private void init(String title, final String tag)
    {
        builder
                .setView(myView)
                .setTitle(title)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Back", null);

        alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int start   = startPicker.getValue();
                        int end     = endPicker.getValue();
                        int step    = stepPicker.getValue();
                        int middle  = defaultValues.length / 2;

                        // The index is different from the actual value, method valid only if the
                        // array is antisymmetric
                        start = start <= middle ? start + 1 : start - maxValue - 1;
                        end   = end   <= middle ? end   + 1 : end   - maxValue - 1;
                        step  = step  <= middle ? step  + 1 : step  - maxValue - 1;

                        // If true, the user is trying to go from a positive value to a negative
                        // value with a positive step or vice versa
                        if ((end - start) / step < 0)
                            Toast.makeText(activity, R.string.wrong_direction, Toast.LENGTH_SHORT)
                                    .show();
                        // If true, start + (n * step) isn't equal to end given any value of n
                        else if ((end - start) % step != 0)
                            Toast.makeText(activity, R.string.step_inadequate, Toast.LENGTH_SHORT)
                                    .show();
                        else if (end == start)
                            Toast.makeText(activity, R.string.one_value_validation,
                                    Toast.LENGTH_SHORT)
                                    .show();
                        else {
                            savePreferences(tag, start, end, step);
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    private void savePreferences(final String tag, int start, int end, int step) {

        SharedPreferences.Editor editor = settings.edit();

        switch (tag) {
            case (C): {
                ModelingStructure.cStart    = start;
                ModelingStructure.cEnd      = end;
                ModelingStructure.cStep     = step;

                editor.putInt(cStartKey, start);
                editor.putInt(cEndKey, end);
                editor.putInt(cStepKey, step);
                editor.apply();
                break;
            }
            case (Gamma): {
                ModelingStructure.gStart    = start;
                ModelingStructure.gEnd      = end;
                ModelingStructure.gStep     = step;

                editor.putInt(gStartKey, start);
                editor.putInt(gEndKey, end);
                editor.putInt(gStepKey, step);
                editor.apply();
                break;
            }
        }
    }

    private void setPickers(final String tag)
    {
        // Initialize startPicker
        startPicker = (NumberPicker) myView.findViewById(R.id.cStartPicker);
        startPicker.setMaxValue(maxValue);
        startPicker.setMinValue(minValue);
        startPicker.setDisplayedValues(defaultValues);

        // Initialize endPicker
        endPicker = (NumberPicker) myView.findViewById(R.id.cEndPicker);
        endPicker.setMinValue(minValue);
        endPicker.setMaxValue(maxValue);
        endPicker.setDisplayedValues(defaultValues);

        // Initialize stepPicker
        stepPicker = (NumberPicker) myView.findViewById(R.id.cStepPicker);
        stepPicker.setMinValue(minValue);
        stepPicker.setMaxValue(maxValue);
        stepPicker.setDisplayedValues(defaultValues);

        int start, end, step;

        switch (tag) {
            // Check if the pickers were previously changed
            case(C):
                start = settings.getInt(cStartKey, 1);
                end   = settings.getInt(cEndKey, 1);
                step  = settings.getInt(cStepKey, 1);
                break;
            case(Gamma):
                start = settings.getInt(gStartKey, 1);
                end   = settings.getInt(gEndKey, 1);
                step  = settings.getInt(gStepKey, 1);
                break;
            default:
                start = 1;
                end   = 1;
                step  = 1;
                break;
        }
        // The index is different from the actual value
        startPicker.setValue(start  < 0 ? start + maxValue + 1 : start - 1);
        endPicker.setValue  (end    < 0 ? end   + maxValue + 1 : end   - 1);
        stepPicker.setValue (step   < 0 ? step  + maxValue + 1 : step  - 1);

    }

    public void showDialog()
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
            View view = inflate(getContext(), R.layout.coef_dialog, null);
            addView(view);
        }
    }
}
