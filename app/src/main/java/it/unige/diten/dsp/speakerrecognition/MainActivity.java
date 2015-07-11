package it.unige.diten.dsp.speakerrecognition;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity
{
    public final static String TAG = "ASR";
    public final static String AUDIO_EXT = ".wav";
    public final static String FEATURE_EXT = ".ff";
    public final static String PATH = Environment.getExternalStorageDirectory() + "/ASR";
    public final static String MODEL_FILENAME = PATH + "/model.model";//"/dummy_g_05_c_05.model";
    public final static String RANGE_FILENAME = PATH + "/range.range";

    public static String fileName;
    public static boolean isTraining;

    public static Context context = null;

    private Button btnRecord = null;
    private EditText etName = null;
    private EditText etDuration = null;
    private RadioButton rbTrain = null;
    private RadioButton rbRecognize = null;
    private static TextView tvResults = null;

    private FEReceiver fe_receiver;
    private SVMReceiver svmReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        btnRecord   = (Button)findViewById(R.id.RecordButton);
        etDuration  = (EditText)findViewById(R.id.edt_Duration);
        etName      = (EditText)findViewById(R.id.edt_Speaker);
        rbRecognize = (RadioButton)findViewById(R.id.rbt_Recognize);
        rbTrain     = (RadioButton)findViewById(R.id.rbt_Train);
        tvResults   = (TextView)findViewById(R.id.tv_Results);

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName = null;
                isTraining = false;

                if (rbTrain.isChecked() && etName.getText().length() != 0) {
                    // Train.
                    fileName = "[" + getCurrentDate() + "]"
                            + etName.getText().toString()
                            + AUDIO_EXT;

                    isTraining = true;
                }

                if (rbRecognize.isChecked()) {
                    // Recognize.
                    fileName = "[" + getCurrentDate() + "]"
                            + AUDIO_EXT;
                }

                if (null != fileName) {
                    Log.i(TAG, "Registration Length (sec): " + (Integer.valueOf(etDuration.getText().toString()) / 1000));
                    Rec rec = new Rec(context, Integer.valueOf(etDuration.getText().toString()) / 1000, 8000);
                    rec.execute(PATH, fileName);
                    // TODO Intent.
                }
            }
        });

        fe_receiver = new FEReceiver();
        context.registerReceiver(fe_receiver, new IntentFilter("it.unige.diten.dsp.speakerrecognition.FEATURE_EXTRACT"));

        svmReceiver = new SVMReceiver();
        context.registerReceiver(svmReceiver, new IntentFilter("it.unige.diten.dsp.speakerrecognition.SVM_EXTRACT"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        unregisterReceiver(fe_receiver);
    }

    private static String getCurrentDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.ITALY);
        Date date = new Date();
        return(dateFormat.format(date));
    }

    public static void updateRecognitionResults(int result)
    {
        String speaker = "";
        switch(result)
        {
            case(0):
                speaker = "Andrea";
                break;
            case(1):
                speaker = "Davide";
                break;
            case(2):
                speaker = "Emanuele";
                break;
            default:
                speaker = "<unknown_speaker>";
                break;
        }

        tvResults.setText("Who spoke?! ");

        String text = tvResults.getText().toString() +
                " " +
                speaker +
                " did speak."
        ;
        tvResults.setText( text );


        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    private static void updatePieChart(String[] names, int[] values)
    {
        pChart.setVisibility(View.VISIBLE);
        pChart.setUsePercentValues(true);

        ArrayList<Entry> percentages = new ArrayList<>();
        int totV = 0;

        for(int i = 0; i < values.length; i++)
        {
            totV += values[i];
        }
        for(int i = 0; i < values.length; i++)
        {
            percentages.add(new Entry((values[i] * 100.0f / totV), i));
        }

        PieDataSet pieDataSet = new PieDataSet(percentages, "Results");
        PieData pieData = new PieData(names, pieDataSet);

        pChart.setData(pieData);
    }

}