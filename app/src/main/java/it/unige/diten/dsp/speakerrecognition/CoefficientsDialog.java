package it.unige.diten.dsp.speakerrecognition;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.internal.widget.ActivityChooserView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by doddo on 8/6/15.
 */
public class CoefficientsDialog extends DialogFragment{

    private AlertDialog dialog;
    private WindowManager.LayoutParams layoutParams;
    private GridView gridView;
    private static MyView myView;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        layoutParams = new WindowManager.LayoutParams();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        myView = new MyView(getActivity());
        builder.setView(myView).setTitle("Coefficient Range");
        builder.setPositiveButton("seghe", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        dialog = builder.create();

        NumberPicker numberPicker = (NumberPicker) myView.findViewById(R.id.numberPicker);

        numberPicker.setMaxValue(1);
        numberPicker.setMaxValue(10);


        return dialog;
    }

    protected void forceWrapContent(View v) {
        // Start with the provided view
        View current = v;

        // Travel up the tree until fail, modifying the LayoutParams
        do {
            // Get the parent
            ViewParent parent = current.getParent();

            // Check if the parent exists
            if (parent != null) {
                // Get the view
                try {
                    current = (View) parent;
                } catch (ClassCastException e) {
                    // This will happen when at the top view, it cannot be cast to a View
                    break;
                }

                // Modify the layout
                current.getLayoutParams().width = WindowManager.LayoutParams.WRAP_CONTENT;
            }
        } while (current.getParent() != null);

        // Request a layout to be re-done
        current.requestLayout();
    }

    public void showDialog()
    {
        dialog.show();
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
