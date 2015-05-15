package it.unige.diten.dsp.speakerrecognition;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity {
    private final static String TAG = "ASR";
    private static String NAME = "";

    Button btnRecord;
    EditText etName;
    EditText etDuration;

    private static Context context;
    short[] samples = null;

    private final String PATH = "ASR";
    private final String FILE_NAME = "AudioRecorder.wav";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        
        btnRecord = (Button)findViewById(R.id.RecordButton);

        etDuration = (EditText)findViewById(R.id.edt_Duration);
        etName = (EditText)findViewById(R.id.edt_Speaker);

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Rec rec = new Rec(context, 5, 8000, samples);
                rec.execute(PATH, FILE_NAME);
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
