package it.unige.diten.dsp.speakerrecognition;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
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

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import it.unige.diten.dsp.speakerrecognition.libsvm.svm;

public class MainActivity extends Activity
{
    public final static String TAG = "ASR";
    public final static String AUDIO_EXT = ".wav";
    public final static String FEATURE_EXT = ".ff";
    public final static String PATH = Environment.getExternalStorageDirectory() + "/ASR";
    public static String MODEL_FILENAME = PATH + "/model.model";//"/dummy_g_05_c_05.model";
    public final static String RANGE_FILENAME = PATH + "/range.range";

    public static int[] SVMResults;

    public static int numCores;

    public static String fileName;
    public static boolean isTraining;

    public static Context context = null;

    private Button btnRecord = null;
    private static EditText etName = null;
    private static EditText etDuration = null;
    private RadioButton rbTrain = null;
    private RadioButton rbRecognize = null;
    private static TextView tvResults = null;
    private static PieChart pChart = null;

    private FEReceiver fe_receiver;
    private SVMReceiver svmReceiver;
    private RecognitionReceiver recognitionReceiver;


    private int getNumCores()
    {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]+", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch(Exception e) {
            //Default to return 1 core
            return 1;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numCores = getNumCores();

        context = this;

        pChart      = (PieChart)findViewById(R.id.chart);
        btnRecord   = (Button)findViewById(R.id.RecordButton);
        etDuration  = (EditText)findViewById(R.id.edt_Duration);
        etName      = (EditText)findViewById(R.id.edt_Speaker);
        rbRecognize = (RadioButton)findViewById(R.id.rbt_Recognize);
        rbTrain     = (RadioButton)findViewById(R.id.rbt_Train);
        tvResults   = (TextView)findViewById(R.id.tv_Results);

        pChart.setDescription("");
/*
        rbRecognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setVisibility(View.INVISIBLE);
            }
        });

        rbTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setVisibility(View.VISIBLE);
            }
        });
*/
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName = null;
                isTraining = false;

                if (rbTrain.isChecked() && etName.getText().length() != 0)
                {
                    // Train.
                    fileName = "[" + getCurrentDate() + "]"
                            + etName.getText().toString()
                            + AUDIO_EXT;

                    isTraining = true;
                }

                if (rbRecognize.isChecked())
                {
                    // Recognize.
                    fileName = "[" + getCurrentDate() + "]"
                            + AUDIO_EXT;
                }

                if (null != fileName)
                {
                    Log.i(TAG, "Registration Length (sec): " + (Integer.valueOf(etDuration.getText().toString()) / 1000));
                    Rec rec = new Rec(context, Integer.valueOf(etDuration.getText().toString()) / 1000 + 1, 8000);  // +1 because of samsung galaxy ace bullshits.
                    rec.execute(PATH, fileName);
                }
            }
        });

        fe_receiver = new FEReceiver();
        context.registerReceiver(fe_receiver, new IntentFilter("it.unige.diten.dsp.speakerrecognition.FEATURE_EXTRACT"));

        svmReceiver = new SVMReceiver();
        context.registerReceiver(svmReceiver, new IntentFilter("it.unige.diten.dsp.speakerrecognition.SVM_EXTRACT"));

        recognitionReceiver = new RecognitionReceiver();
        context.registerReceiver(recognitionReceiver, new IntentFilter("it.unige.diten.dsp.speakerrecognition.UPDATE_RECOGNITION"));
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
        unregisterReceiver(svmReceiver);
    }

    private static String getCurrentDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.ITALY);
        Date date = new Date();
        return(dateFormat.format(date));
    }

    public static void updateRecognitionResults(int result)
    {
        String[] names = {"Andrea", "Davide", "Emanuele"};
        String speaker;
        switch(result)
        {
            case(0):
            case(1):
            case(2):
                speaker = names[result];
                break;
            default:
                speaker = "<unknown_speaker>";
                break;
        }

        String text =
                " " +
                speaker +
                " did speak."
        ;
        tvResults.setText(text);

        updatePieChart(names, SVMResults);

        writeResultFile(PATH + "/results.txt");

        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private static void updatePieChart(String[] names, int[] values)
    {
        // By default the chart is not visible
        pChart.setVisibility(View.VISIBLE);
        // The output is automatically in percentages
        pChart.setUsePercentValues(true);
        // Write the x-value on the chart
        pChart.setDrawSliceText(false);
        //You spin the chart round, baby right round like a record, baby, right round round round
        pChart.setDragDecelerationFrictionCoef(0.95f);

        // List of Entry(float val, int index), necessary for the ChartDataSet
        ArrayList<Entry> results = new ArrayList<>();

        for(int i = 0; i < values.length; i++)
        {
            results.add(new Entry(values[i], i));
        }

        // Create a new data set for the pie chart with the desired values
        PieDataSet pieDataSet = new PieDataSet(results, "");
        // Set colors for each slice
        pieDataSet.setColors(new int[] {Color.rgb(164,230,255), Color.rgb(0,185,255), Color.rgb(0,116,159)});
        // Space between slices (in degrees
        pieDataSet.setSliceSpace(5f);
        // Create the data that will be displayed by the chart
        PieData pieData = new PieData(names, pieDataSet);

        pChart.setRotationAngle(0);
        // Impress an initial spin that fades away within 3 seconds and makes the pie rotate 720 degrees
        pChart.spin(3000,0,720, Easing.EasingOption.EaseOutCubic);

        // Display the data
        pChart.setData(pieData);
        pChart.refreshDrawableState();
    }

    private static void writeResultFile(String filename)
    {
        try
        {
            File file = new File(filename);
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.append("Andrea = ");
            fileWriter.append(String.valueOf(SVMResults[0]));
            fileWriter.append("\tDavide = ");
            fileWriter.append(String.valueOf(SVMResults[1]));
            fileWriter.append("\tEmanuele = ");
            fileWriter.append(String.valueOf(SVMResults[2]));
            fileWriter.append("\tActualSpeaker = " + etName.getText().toString());
            fileWriter.append("\tSeconds = " + String.valueOf((Double.valueOf(etDuration.getText().toString())/1000)));
            fileWriter.append("\tModel = " + MODEL_FILENAME);
            fileWriter.append("\n");
            fileWriter.close();
        }
        catch(Exception ew)
        {
            //shtua fako
        }
    }
}