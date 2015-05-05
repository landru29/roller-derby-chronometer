package fr.noopy.landru.rollerderbychronometers.listeners;

import fr.noopy.landru.rollerderbychronometers.components.CountDownChronometer;

/**
 * Created by cyrille on 05/05/15.
 */
public abstract class OnCountDownTickListener {

    public abstract void onChronometerTick(CountDownChronometer countDownChronometer);
}
