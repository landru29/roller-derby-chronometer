package fr.noopy.landru.rollerderbychronometers.components;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import fr.noopy.landru.rollerderbychronometers.listeners.OnCountDownTickListener;

/**
 * Created by cyrille on 05/05/15.
 */
public class CountDownChronometer extends TextView {

    private long value;
    private Timer timer;
    private Activity currentActivity;
    private boolean pause;
    private OnCountDownTickListener tickListener;

    /**
     * Constructor
     * @param context
     * @param attrs
     * @param defStyle
     */
    public CountDownChronometer(Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
        this.currentActivity = (Activity) context;
        setText("0:00");
        this.pause = false;
        this.tickListener = null;
    }

    /**
     * Constructor
     * @param context
     */
    public CountDownChronometer(Context context) {
        this(context, null);
    }

    /**
     * Constructor
     * @param context
     * @param attrs
     */
    public CountDownChronometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Set chronometer value
     * @param milliseconds
     */
    public void setValue(long milliseconds) {
        this.value = (milliseconds>0 ? milliseconds : 0);
        display();
    }

    /**
     * Add time to the chronometer
     * @param milliseconds
     */
    public void addValue(long milliseconds) {
        this.value += milliseconds;
        display();
    }

    /**
     * Start the chronometer
     */
    public void start() {
        stop();
        final CountDownChronometer me = this;
        final Runnable task = new Runnable() {
            @Override
            public void run() {
                if (!pause) {
                    value -= 10;
                    if (value <= 0) {
                        value = 0;
                        stop();
                    }
                    display();
                    if (tickListener != null) {
                        tickListener.onChronometerTick(me);
                    }
                }
            }
        };

        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (task != null) {
                    currentActivity.runOnUiThread(task);
                }
            }
        }, 0, 10);
    }

    private void display() {
        int seconds = (int) (Math.ceil((double)value / 1000)) % 60;
        int minutes = (int) (((value + 999) / (1000*60)) % 60);
        setText(String.format("%d:%02d", minutes, seconds));
    }

    /**
     * Stop the chronometer
     */
    public void stop() {
        if (this.timer != null) {
            this.timer.cancel();
        }
        this.timer = null;
    }

    /**
     * Toggle pause
     */
    public void togglePause() {
        this.pause = !this.pause;
    }

    /**
     * Set the pause state
     * @param state
     */
    public void setPauseState(boolean state) {
        this.pause = state;
    }

    /**
     * Get the chronometer value
     * @return
     */
    public long getValue() {
        return value;
    }

    /**
     * Get the state of the chronometer
     * @return
     */
    public Bundle getState() {
        Bundle bundle = new Bundle();
        bundle.putLong("system-clock", SystemClock.elapsedRealtime());
        bundle.putLong("value", value);
        bundle.putBoolean("is-running", isRunning());
        return bundle;
    }

    /**
     * Restore the state of the chronometer
     * @param bundle
     */
    public void setState(Bundle bundle) {
        long offset = SystemClock.elapsedRealtime() - bundle.getLong("system-clock");
        setValue(bundle.getLong("value") - offset);
        if (bundle.getBoolean("is-running")) {
            start();
        }
    }

    /**
     * Get the status of the chronometer
     * @return
     */
    public boolean isRunning() {
        return (this.timer != null);
    }

    /**
     * Get the pause status
     * @return
     */
    public boolean isPaused() {
        return this.pause;
    }

    public void setOnChronometerTickListener(OnCountDownTickListener listener) {
        this.tickListener = listener;
    }
}

