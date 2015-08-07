package it.unige.diten.dsp.speakerrecognition;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import it.unige.diten.dsp.speakerrecognition.Structures.ModelingStructure;

/**
 * Created by doddo on 8/6/15.
 */
public class CoefficientsDialog extends DialogFragment{


    private MyView myView;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private NumberPicker startPicker, endPicker, stepPicker;

    final private String tag    = getTag();
    final private String C      = "C_Coefficient";
    final private String Gamma  = "Gamma_Coefficient";
    final private String cTitle = "C coefficient range";
    final private String gTitle = "Gamma coefficient range";

    String[] defaultValues = new String[] {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
            "12", "13", "14", "15", "-15", "-14", "-13", "-12", "-11",
            "-10", "-9", "-8","-7", "-6", "-5", "-4", "-3", "-2", "-1",
    };



    final int minValue = 0;
    final int maxValue = defaultValues.length - 1;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());
        myView = new MyView(getActivity());

        switch(tag)
        {
            case (C):
                init(cTitle, tag);
                break;
            case (Gamma):
                init(gTitle, tag);
                break;
            default:
                dismiss();
                break;
        }

        setPickers();

        return alertDialog;
    }

    private void init(String title, final String tag)
    {
        builder.setView(myView).setTitle(title);
        builder.setPositiveButton("Ok", null);

        alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int start   = startPicker.getValue();
                        int end     = endPicker.getValue();
                        int step    = stepPicker.getValue();

                        // The index is different from the actual value
                        start = start   <= 16 ? start + 1   : start - maxValue - 1;
                        end   = end     <= 16 ? end   + 1   : end   - maxValue - 1;
                        step  = step    <= 16 ? step  + 1   : step  - maxValue - 1;

                        if ((end - start) / step < 0)
                            Toast.makeText(getActivity(),
                                    "Step is in the wrong direction!", Toast.LENGTH_SHORT)
                                    .show();

                        else if ((end - start) % step != 0)
                            Toast.makeText(getActivity(),
                                    "Step inadequate for selected boundaries.", Toast.LENGTH_SHORT)
                                    .show();
                        else if (end == start)
                        {
                            Toast.makeText(getActivity(),
                                    "You can try a one-value cross validation in another preference screen.", Toast.LENGTH_SHORT)
                                    .show();

                        }
                        else {
                            switch(tag) {
                                case(C): {
                                    ModelingStructure.cStart = start;
                                    ModelingStructure.cEnd = end;
                                    ModelingStructure.cStep = step;
                                    alertDialog.dismiss();
                                    break;
                                }
                                case(Gamma):
                                {
                                    ModelingStructure.gStart = start;
                                    ModelingStructure.gEnd = end;
                                    ModelingStructure.gStep = step;
                                    alertDialog.dismiss();
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    private void setPickers()
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
