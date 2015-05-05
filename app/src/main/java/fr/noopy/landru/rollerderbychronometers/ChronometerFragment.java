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

import fr.noopy.landru.rollerderbychronometers.components.CountDownChronometer;
import fr.noopy.landru.rollerderbychronometers.listeners.OnCountDownTickListener;

/**
 * A fragment with a Google +1 button.
 */
public class ChronometerFragment extends Fragment {

    enum chronometerType {
        JAMMER,
        BLOCKER
    };

    private chronometerType type;

    public ChronometerFragment() {
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
                if (action.equals("start-stop")){
                    CountDownChronometer chrono = (CountDownChronometer)getView().findViewById(R.id.chronometer);
                    String state = intent.getExtras().getString("action");
                    if (state.compareTo("start") == 0) {
                        chrono.setPauseState(false);
                    }
                    if (state.compareTo("stop") == 0) {
                        chrono.setPauseState(true);
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
        if (bundle != null) {
            if (bundle.containsKey("name")) {
                TextView nameView = (TextView) getView().findViewById(R.id.name);
                nameView.setText(bundle.getString("name"));
            }
            if (bundle.containsKey("type")) {
                this.type = chronometerType.values()[bundle.getInt("type", 0)];
            }
        }

        final CountDownChronometer chrono = (CountDownChronometer)getView().findViewById(R.id.chronometer);
        final TextView instruction = (TextView)getView().findViewById(R.id.instruction);
        final Button startStop = (Button)getView().findViewById(R.id.start_stop);

        chrono.setOnChronometerTickListener(new OnCountDownTickListener() {
            @Override
            public void onChronometerTick(CountDownChronometer countDownChronometer) {
                long value = countDownChronometer.getValue();
                if (value<10000) {
                    instruction.setText(R.string.instruction_stand);
                }
                if (value == 0) {
                    instruction.setText(R.string.instruction_done);
                    startStop.setText(R.string.chronometer_start);
                }
            }
        });

        startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chrono.isRunning()) {
                    chrono.stop();
                    startStop.setText(R.string.chronometer_start);
                } else {
                    chrono.setValue(30000);
                    instruction.setText("");
                    startStop.setText(R.string.chronometer_stop);
                    chrono.start();

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
