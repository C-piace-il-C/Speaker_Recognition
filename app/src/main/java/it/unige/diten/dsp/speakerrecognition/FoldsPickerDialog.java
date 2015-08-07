package it.unige.diten.dsp.speakerrecognition;

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

import it.unige.diten.dsp.speakerrecognition.Structures.ModelingStructure;

public class FoldsPickerDialog extends DialogFragment{

    private Activity            activity;
    private AlertDialog.Builder builder;
    private AlertDialog         alertDialog;
    private NumberPicker        numberPicker;
    private MyView              myView;

    private final int           minValue = 2;
    private final int           maxValue = 10;

    private String foldsKey;

    private SharedPreferences settings;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.activity = getActivity();

        builder = new AlertDialog.Builder(activity);
        myView  = new MyView(activity);
        settings    = PreferenceManager.getDefaultSharedPreferences(activity);

        foldsKey = getString(R.string.folds_key);

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
                .setTitle("Folds")
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ModelingStructure.folds = numberPicker.getValue();
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt(foldsKey, ModelingStructure.folds);
                        editor.apply();
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private void setPickers()
    {
        numberPicker = (NumberPicker) myView.findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setMinValue(minValue);
        numberPicker.setValue(settings.getInt(foldsKey, 2));
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
            View view = inflate(getContext(), R.layout.number_picker, null);
            addView(view);
        }
    }
}
