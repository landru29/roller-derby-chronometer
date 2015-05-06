package fr.noopy.landru.rollerderbychronometers;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private int team;
    private boolean fullTime;

    private BroadcastReceiver penaltyBoxStop;
    private BroadcastReceiver penaltyBoxJammerArriveInBox;
    private BroadcastReceiver PenaltyBoxTimeToServe;

    public ChronometerFragment() {
        this.type = chronometerType.BLOCKER;
        this.fullTime = true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chronometer, container, false);

        registerPenaltyBoxStop();

        return view;
    }

    private void registerPenaltyBoxStop() {
        penaltyBoxStop = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.i("Receiver", "Broadcast received: " + action);
                if (action.equals("Penalty-Box_start-stop")){
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
        };
        getActivity().registerReceiver(penaltyBoxStop, new IntentFilter("Penalty-Box_start-stop"));
    }

    private void registerJammerCommunication() {
        final CountDownChronometer chrono = (CountDownChronometer)getView().findViewById(R.id.chronometer);
        final TextView instruction = (TextView)getView().findViewById(R.id.instruction);
        final Button startStop = (Button)getView().findViewById(R.id.start_stop);
        final LinearLayout container = (LinearLayout)getView().findViewById(R.id.container);

        penaltyBoxJammerArriveInBox = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ((intent.getAction().equals("Penalty-Box_jammer-arrive-in-box")) && (team != intent.getExtras().getInt("team"))) {
                    // the other jammer is comming to penalty box
                    Log.i("JAMMER", "comming to box team: " + intent.getExtras().getInt("team"));
                    Intent data = new Intent("Penalty-Box_time-to-serve");
                    data.putExtra("team", team);
                    if (chrono.isRunning()) {
                        // this jammer is in box while the other is comming
                        if (fullTime) {
                            data.putExtra("full-time", false);
                            data.putExtra("milliseconds", chrono.getValue());
                            chrono.stop();
                            chrono.setValue(0);
                            instruction.setText(R.string.instruction_done);
                        } else {
                            data.putExtra("milliseconds", (long)30000);
                            data.putExtra("full-time", true);

                            chrono.stop();
                            chrono.setValue(0);
                            instruction.setText(R.string.instruction_done);
                        }
                    } else {
                        data.putExtra("milliseconds", (long)30000);
                        data.putExtra("full-time", true);
                    }
                    getActivity().sendBroadcast(data);
                }
            }
        };
        getActivity().registerReceiver(penaltyBoxJammerArriveInBox, new IntentFilter("Penalty-Box_jammer-arrive-in-box"));

        PenaltyBoxTimeToServe = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ((action.equals("Penalty-Box_time-to-serve")) && (intent.getExtras().getInt("team") != team) &&  (!chrono.isRunning())) {
                    Log.i("JAMMER", "Time to serve: " + intent.getExtras().getLong("milliseconds"));
                    long timeToPurge = intent.getExtras().getLong("milliseconds");
                    fullTime = intent.getExtras().getBoolean("full-time");
                    instruction.setText("");
                    startStop.setText(R.string.chronometer_stop);
                    chrono.setValue(timeToPurge);
                    chrono.start();
                    setOnRunning(true);
                }
            }
        };
        getActivity().registerReceiver(PenaltyBoxTimeToServe, new IntentFilter("Penalty-Box_time-to-serve"));

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ImageView icon = (ImageView)getView().findViewById(R.id.icon);
        final CountDownChronometer chrono = (CountDownChronometer)getView().findViewById(R.id.chronometer);
        final TextView instruction = (TextView)getView().findViewById(R.id.instruction);
        final Button startStop = (Button)getView().findViewById(R.id.start_stop);
        final LinearLayout container = (LinearLayout)getView().findViewById(R.id.container);

        loadParameters(this.getArguments());

        switch (this.type) {
            case JAMMER:
                icon.setImageResource(R.drawable.star);
                break;
            default:
                icon.setImageResource(R.drawable.circle);
        }

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
                    container.setBackgroundResource(0);
                }
            }
        });

        startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == chronometerType.JAMMER) {
                    jammerStartStop();
                } else {
                    blockerStartStop();
                }
            }
        });

        if (this.type == chronometerType.JAMMER) {
            registerJammerCommunication();
        }


    }

    public void loadParameters(Bundle bundle) {
        if (bundle != null) {
            if (bundle.containsKey("full-time")) {
                fullTime = bundle.getBoolean("full-time");
            }
            if (bundle.containsKey("type")) {
                this.type = chronometerType.values()[bundle.getInt("type", 0)];
            }
            if (bundle.containsKey("team")) {
                this.team = bundle.getInt("team", 0);
            }
            if (bundle.containsKey("chronometer")) {
                CountDownChronometer chrono = (CountDownChronometer)getView().findViewById(R.id.chronometer);
                chrono.setState(bundle.getBundle("chronometer"));
                if (chrono.isRunning()) {
                    setOnRunning(true);
                }
            }
        }
    }

    public Bundle saveMe() {
        CountDownChronometer chrono = (CountDownChronometer)getView().findViewById(R.id.chronometer);
        Bundle bundle = new Bundle();
        bundle.putBoolean("full-time", fullTime);
        bundle.putInt("team", team);
        bundle.putInt("type", type.ordinal());
        bundle.putBundle("chronometer", chrono.getState());
        return bundle;
    }

    public void blockerStartStop() {
        CountDownChronometer chrono = (CountDownChronometer)getView().findViewById(R.id.chronometer);
        TextView instruction = (TextView)getView().findViewById(R.id.instruction);
        Button startStop = (Button)getView().findViewById(R.id.start_stop);
        LinearLayout container = (LinearLayout)getView().findViewById(R.id.container);
        if (chrono.isRunning()) {
            chrono.stop();
            startStop.setText(R.string.chronometer_start);
            container.setBackgroundResource(0);
        } else {
            chrono.setValue(30000);
            instruction.setText("");
            startStop.setText(R.string.chronometer_stop);
            chrono.start();
            setOnRunning(true);
        }
    }

    public void jammerStartStop() {
        CountDownChronometer chrono = (CountDownChronometer)getView().findViewById(R.id.chronometer);
        Button startStop = (Button)getView().findViewById(R.id.start_stop);
        LinearLayout container = (LinearLayout)getView().findViewById(R.id.container);
        if (chrono.isRunning()) {
            chrono.stop();
            startStop.setText(R.string.chronometer_start);
            setOnRunning(false);
        } else {
            Intent data = new Intent("Penalty-Box_jammer-arrive-in-box");
            data.putExtra("team", team);
            getActivity().sendBroadcast(data);
        }
    }

    public void setOnRunning(boolean state) {
        LinearLayout container = (LinearLayout)getView().findViewById(R.id.container);
        if (state) {
            container.setBackgroundResource(R.drawable.animated_on_going);
            AnimationDrawable frameAnimation = (AnimationDrawable) container.getBackground();
            frameAnimation.start();
        } else {
            container.setBackgroundResource(0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives focus.

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (penaltyBoxStop!=null) {
            getActivity().unregisterReceiver(penaltyBoxStop);
        }
        if (penaltyBoxJammerArriveInBox!=null) {
            getActivity().unregisterReceiver(penaltyBoxJammerArriveInBox);
        }
        if (PenaltyBoxTimeToServe!=null) {
            getActivity().unregisterReceiver(PenaltyBoxTimeToServe);
        }
    }

}
