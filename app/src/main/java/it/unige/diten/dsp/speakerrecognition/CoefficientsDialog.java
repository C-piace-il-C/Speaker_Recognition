package it.unige.diten.dsp.speakerrecognition;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Created by doddo on 8/6/15.
 */
public class CoefficientsDialog {

    private final Activity activity;
    private AlertDialog dialog;
    private WindowManager.LayoutParams layoutParams;

    public CoefficientsDialog(Activity activity)
    {
        this.activity = activity;

        layoutParams = new WindowManager.LayoutParams();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //builder.setView(R.layout.coef_dialog);


        dialog = builder.create();
        //layoutParams.copyFrom(dialog.getWindow().getAttributes());
        //layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;


    }

    public void showDialog()
    {
        dialog.show();
        //dialog.getWindow().setAttributes(layoutParams);
    }
}
