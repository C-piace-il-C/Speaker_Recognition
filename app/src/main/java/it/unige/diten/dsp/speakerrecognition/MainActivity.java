package it.unige.diten.dsp.speakerrecognition;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//import android.provider.MediaStore;

public class MainActivity extends ActionBarActivity {
    private final static String TAG = "ASR";
    private static String NAME = "";

    private Button btnRecord;
    private EditText etName;
    private EditText etDuration;
    private RadioButton rbTrain;
    private RadioButton rbRecognize;

    public final String AUDIO_EXTENSION = ".wav";
    public final String FEATURE_EXTENSION = ".ff";
    private final String PATH = "ASR";

    private static String getCurrentDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        return(dateFormat.format(date).toString());
    }

    private static Context context;
    short[] samples = null;

    private String fileName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        btnRecord = (Button)findViewById(R.id.RecordButton);
        etDuration = (EditText)findViewById(R.id.edt_Duration);
        etName = (EditText)findViewById(R.id.edt_Speaker);
        rbRecognize = (RadioButton)findViewById(R.id.rbt_Recognize);
        rbTrain = (RadioButton)findViewById(R.id.rbt_Train);

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etName.getText().length() != 0) {
                    // Gets text from edt_Name, FORMAT: "[yyyyMMddHHmmss] Name.wav"
                    fileName = "[" + getCurrentDate() + "] " + etName.getText().toString() + AUDIO_EXTENSION;

                    RecAndRell rec = new RecAndRell(context, 5, 8000, samples);
                    rec.execute(PATH, fileName);

                    if(rbTrain.isChecked()) {
                        // Extract features with AsyncTask and store them in a file.
                        FeatureExtractor fe = new FeatureExtractor();
                        Toast toast = Toast.makeText(
                            context, "come se fosse: " + PATH + "/" + fileName, Toast.LENGTH_SHORT);
                        toast.show();
                        fe.execute(PATH + "/" + fileName);

                    } else {// Recognition
                        // Send WAV to the SVM server
                    }
                } else {
                    Log.e(TAG, "Record: No speaker name specified!");
                    Toast toast = Toast.makeText(
                            context, "Speaker name cannot be empty!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
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
}
