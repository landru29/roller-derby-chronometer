package fr.noopy.landru.rollerderbychronometers;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

/**
 * A fragment with a Google +1 button.
 */
public class ChronometerFragment extends Fragment {

    private boolean running;
    private boolean globalRunning;
    private long pauseTimeMilli;

    public ChronometerFragment() {
        this.running = false;
        this.globalRunning = true;
        this.pauseTimeMilli = 0;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chronometer, container, false);

        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.i("Receiver", "Broadcast received: " + action);
                if(action.equals("start-stop")){
                    Chronometer chrono = (Chronometer)getView().findViewById(R.id.chronometer);
                    String state = intent.getExtras().getString("action");
                    if (state.compareTo("start") == 0) {
                        globalRunning = true;
                        if (running) {
                            chrono.setBase(SystemClock.elapsedRealtime() + pauseTimeMilli);
                            chrono.start();
                        }
                    }
                    if (state.compareTo("stop") == 0) {
                        globalRunning = false;
                        if (running) {
                            pauseTimeMilli = chrono.getBase() - SystemClock.elapsedRealtime();
                            chrono.stop();
                        }
                    }
                }
            }
        }, new IntentFilter("start-stop"));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = this.getArguments();
        if ((bundle != null) && (bundle.containsKey("name"))) {
            TextView nameView = (TextView)getView().findViewById(R.id.name);
            nameView.setText(bundle.getString("name"));
        }

        final Chronometer chrono = (Chronometer)getView().findViewById(R.id.chronometer);
        final TextView instruction = (TextView)getView().findViewById(R.id.instruction);
        final Button startStop = (Button)getView().findViewById(R.id.start_stop);

        chrono.setText("0:00");
        chrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                CharSequence text = chronometer.getText();
                if (text.length()>4) {
                    chronometer.setText(text.subSequence(text.length()-4, text.length()));
                } else {
                    chronometer.setText(text);
                }
                long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                if ((elapsedMillis>20000) && (elapsedMillis<30000)) {
                    instruction.setText(R.string.instruction_stand);
                }
                if (elapsedMillis>30000) {
                    instruction.setText(R.string.instruction_done);
                    chronometer.stop();
                    running = false;
                    startStop.setText(R.string.chronometer_start);
                    pauseTimeMilli = 0;
                }
            }
        });

        startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running) {
                    // stop the chronometer
                    chrono.stop();
                    running = false;
                    startStop.setText(R.string.chronometer_start);
                } else {
                    // start the chronometer
                    chrono.setBase(SystemClock.elapsedRealtime());
                    chrono.setText("0:00");
                    pauseTimeMilli = 0;
                    instruction.setText("");
                    running = true;
                    startStop.setText(R.string.chronometer_stop);
                    if (globalRunning) {
                        chrono.start();
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives focus.

    }


}
