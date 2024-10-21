package com.example.stoptimer;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TimerManager {
    private final String TAG = "NRJ_"+TimerManager.class.getSimpleName();
    private final Map<String, Timer> mTimers = new HashMap<>();
    private final TimerListener mListener;
    public TimerManager(TimerListener listener) {
        mListener = listener;
    }

    public boolean setTimer(final String name, final long durationInSec) {
        if (mTimers.containsKey(name)) {
            Log.w(TAG, "The timer[" + name + "] already exists.");
            return false;
        }
        if (durationInSec <= 0) {
            Log.w(TAG, "Invalid timer value for: " + name);
            return false;
        }

        Timer timer = new Timer(name);
        TimerEndTask task = new TimerEndTask(mListener);
        mTimers.put(name, timer);
        timer.schedule(task, durationInSec * 1000);
        mListener.timeIsOn(name);

        Log.d(TAG, "Timer[" + name + "] with duration: " + durationInSec + " seconds has started.");

        return true;
    }

    public void stopTimer(final String name) {
        if (mTimers.containsKey(name)) {
            Timer timer = mTimers.get(name);
            timer.cancel();
            timer.purge();

            mTimers.remove(name);
            mListener.timeIsOff(name);

            Log.d(TAG, "Timer[" + name + "] cancelled.");
        }
    }

    public interface TimerListener {
        void timeIsOn(final String timerName);
        void timeIsUp(final String timerName);
        void timeIsOff(final String timerName);
    }

    private class TimerEndTask extends TimerTask {
        private final TimerListener mListener;
        private boolean hasStarted = false;

        public TimerEndTask(TimerListener listener) {
            mListener = listener;
        }

        @Override
        public void run() {
            this.hasStarted = true;
            String name = Thread.currentThread().getName();
            mListener.timeIsUp(name);
        }

        public boolean hasRunStarted() {
            return this.hasStarted;
        }
    }


}
