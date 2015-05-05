package fr.noopy.landru.rollerderbychronometers;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        running = true;
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            this.getFragmentManager().beginTransaction().replace(R.id.chrono00, penaltyBoxChronometer(ChronometerFragment.chronometerType.BLOCKER, "B13")).commit();
            this.getFragmentManager().beginTransaction().replace(R.id.chrono01, penaltyBoxChronometer(ChronometerFragment.chronometerType.BLOCKER, "B12")).commit();
            this.getFragmentManager().beginTransaction().replace(R.id.chrono02, penaltyBoxChronometer(ChronometerFragment.chronometerType.BLOCKER, "B13")).commit();
            this.getFragmentManager().beginTransaction().replace(R.id.chrono0J, penaltyBoxChronometer(ChronometerFragment.chronometerType.JAMMER, "J")).commit();

            this.getFragmentManager().beginTransaction().replace(R.id.chrono1J, penaltyBoxChronometer(ChronometerFragment.chronometerType.JAMMER, "J2")).commit();
            this.getFragmentManager().beginTransaction().replace(R.id.chrono10, penaltyBoxChronometer(ChronometerFragment.chronometerType.BLOCKER, "B21")).commit();
            this.getFragmentManager().beginTransaction().replace(R.id.chrono11, penaltyBoxChronometer(ChronometerFragment.chronometerType.BLOCKER, "B22")).commit();
            this.getFragmentManager().beginTransaction().replace(R.id.chrono12, penaltyBoxChronometer(ChronometerFragment.chronometerType.BLOCKER, "B23")).commit();

        }

        final Button globalPause = (Button)findViewById(R.id.pause_button);
        globalPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent("start-stop");
                if (running) {
                    running = false;
                    Log.i("GLOBAL ACTION", "stop");
                    data.putExtra("action", "stop");
                    globalPause.setText(R.string.resume_button_caption);
                } else {
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

    private ChronometerFragment penaltyBoxChronometer(ChronometerFragment.chronometerType type, String name) {
        ChronometerFragment chrono = new ChronometerFragment();

        Bundle bundle = new Bundle();

        bundle.putInt("type", type.ordinal());
        bundle.putString("name", name);
        chrono.setArguments(bundle);
        return chrono;
    }
}
