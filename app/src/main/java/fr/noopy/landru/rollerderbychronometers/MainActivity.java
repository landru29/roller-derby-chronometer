package fr.noopy.landru.rollerderbychronometers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import fr.noopy.landru.rollerderbychronometers.components.CountDownChronometer;


public class MainActivity extends ActionBarActivity {

    private boolean running;

    private ArrayList<ChronometerFragment> chronometers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        running = true;
        chronometers = new ArrayList<ChronometerFragment>();
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            this.getFragmentManager().beginTransaction().replace(R.id.chrono00, penaltyBoxChronometer(ChronometerFragment.chronometerType.BLOCKER, 0)).commit();
            this.getFragmentManager().beginTransaction().replace(R.id.chrono01, penaltyBoxChronometer(ChronometerFragment.chronometerType.BLOCKER, 0)).commit();
            this.getFragmentManager().beginTransaction().replace(R.id.chrono02, penaltyBoxChronometer(ChronometerFragment.chronometerType.BLOCKER, 0)).commit();
            this.getFragmentManager().beginTransaction().replace(R.id.chrono0J, penaltyBoxChronometer(ChronometerFragment.chronometerType.JAMMER, 0)).commit();

            this.getFragmentManager().beginTransaction().replace(R.id.chrono1J, penaltyBoxChronometer(ChronometerFragment.chronometerType.JAMMER, 1)).commit();
            this.getFragmentManager().beginTransaction().replace(R.id.chrono10, penaltyBoxChronometer(ChronometerFragment.chronometerType.BLOCKER, 1)).commit();
            this.getFragmentManager().beginTransaction().replace(R.id.chrono11, penaltyBoxChronometer(ChronometerFragment.chronometerType.BLOCKER, 1)).commit();
            this.getFragmentManager().beginTransaction().replace(R.id.chrono12, penaltyBoxChronometer(ChronometerFragment.chronometerType.BLOCKER, 1)).commit();

        } else {
            Log.i("RESTORE", "Restoring state");
            chronometers.clear();
            if (savedInstanceState.containsKey("chrono0")) {
                this.getFragmentManager().beginTransaction().replace(R.id.chrono00, penaltyBoxChronometer(savedInstanceState.getBundle("chrono0"))).commit();
            }
            if (savedInstanceState.containsKey("chrono1")) {
                this.getFragmentManager().beginTransaction().replace(R.id.chrono01, penaltyBoxChronometer(savedInstanceState.getBundle("chrono1"))).commit();
            }
            if (savedInstanceState.containsKey("chrono2")) {
                this.getFragmentManager().beginTransaction().replace(R.id.chrono02, penaltyBoxChronometer(savedInstanceState.getBundle("chrono1"))).commit();
            }
            if (savedInstanceState.containsKey("chrono3")) {
                this.getFragmentManager().beginTransaction().replace(R.id.chrono0J, penaltyBoxChronometer(savedInstanceState.getBundle("chrono3"))).commit();
            }
            if (savedInstanceState.containsKey("chrono4")) {
                this.getFragmentManager().beginTransaction().replace(R.id.chrono1J, penaltyBoxChronometer(savedInstanceState.getBundle("chrono4"))).commit();
            }
            if (savedInstanceState.containsKey("chrono5")) {
                this.getFragmentManager().beginTransaction().replace(R.id.chrono10, penaltyBoxChronometer(savedInstanceState.getBundle("chrono5"))).commit();
            }
            if (savedInstanceState.containsKey("chrono6")) {
                this.getFragmentManager().beginTransaction().replace(R.id.chrono11, penaltyBoxChronometer(savedInstanceState.getBundle("chrono6"))).commit();
            }
            if (savedInstanceState.containsKey("chrono7")) {
                this.getFragmentManager().beginTransaction().replace(R.id.chrono12, penaltyBoxChronometer(savedInstanceState.getBundle("chrono7"))).commit();
            }

            if (savedInstanceState.containsKey("jam-chrono")) {
                CountDownChronometer jamChrono = (CountDownChronometer)findViewById(R.id.jam_chronometer);
                jamChrono.setState(savedInstanceState.getBundle("jam-chrono"));
            }

        }

        final Button globalPause = (Button)findViewById(R.id.pause_button);
        globalPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent("Penalty-Box_start-stop");
                EditText jamNumber = (EditText)findViewById(R.id.jam_number);
                CountDownChronometer jamChronometer = (CountDownChronometer)findViewById(R.id.jam_chronometer);
                if (running) {
                    running = false;
                    Log.i("GLOBAL ACTION", "stop");
                    data.putExtra("action", "stop");
                    globalPause.setText(R.string.resume_button_caption);
                    jamChronometer.stop();
                } else {
                    int jam = Integer.parseInt(jamNumber.getText().toString()) + 1;
                    jamNumber.setText("" + jam);
                    jamChronometer.setValue(120000);
                    jamChronometer.start();
                    running = true;
                    data.putExtra("action", "start");
                    Log.i("GLOBAL ACTION", "start");
                    globalPause.setText(R.string.pause_button_caption);
                }
                sendBroadcast(data);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private ChronometerFragment penaltyBoxChronometer(ChronometerFragment.chronometerType type, int team) {
        ChronometerFragment chrono = new ChronometerFragment();

        Bundle bundle = new Bundle();

        bundle.putInt("type", type.ordinal());
        bundle.putInt("team", team);
        chrono.setArguments(bundle);

        chronometers.add(chrono);

        return chrono;
    }

    private ChronometerFragment penaltyBoxChronometer(Bundle bundle) {
        ChronometerFragment chrono = new ChronometerFragment();
        chrono.setArguments(bundle);
        chronometers.add(chrono);
        return chrono;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        int i=0;
        for (ChronometerFragment chrono:chronometers) {
            savedInstanceState.putBundle("chrono" + (i++), chrono.saveMe());
        }
        CountDownChronometer jamChrono = (CountDownChronometer)findViewById(R.id.jam_chronometer);
        savedInstanceState.putBundle("jam-chrono", jamChrono.getState());
    }
}
