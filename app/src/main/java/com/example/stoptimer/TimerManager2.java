package com.example.stoptimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TimerManager2 {
    private final String TAG = "NRJ_"+TimerManager2.class.getSimpleName();

    private final Map<String, TimerData> mTimers = new HashMap<>();
    private static final String TIME_SET = "android.intent.action.TIME_SET";
    private final TimerListener mListener;
    private IntentFilter mIntentFilter;
    private BroadcastReceiver mTimeChangeReceiver;

    public TimerManager2(TimerListener listener, Context context) {
        mListener = listener;

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(TIME_SET);
        mTimeChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(TIME_SET)) {
                    Log.d(TAG, "Date Time changed:"+SystemClock.elapsedRealtime());
                    for (Map.Entry<String, TimerData> entry : mTimers.entrySet()) {
                        TimerData timerData = entry.getValue();
                        if(timerData.endTime < SystemClock.elapsedRealtime()){
                            Log.d(TAG, "Time already passed !, So stopping");
                            timerData.timer.cancel();
                            mListener.timeIsUp(entry.getValue().name);
                        }
                        else {
                            long elapsedRealtime = SystemClock.elapsedRealtime();
                            long pendingTimer = timerData.getEndTime() - elapsedRealtime;
                            Log.d(TAG, "Time left we will start the fresh timer pending time"+pendingTimer);

                            if(pendingTimer < 0){
                                Log.e(TAG, "onReceive: Invalid time check for correctness ---");
                            }

                            timerData.timer.cancel();
                            Timer newTimer = new Timer(timerData.name);
                            timerData.setTimer(newTimer);
                            TimerEndTask task = new TimerEndTask(timerData.name, mListener);
                            timerData.timer.schedule(task, pendingTimer);
                        }
                    }
                }
            }
        };
        context.registerReceiver(mTimeChangeReceiver,mIntentFilter);
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

        long endTime = SystemClock.elapsedRealtime() + durationInSec * 1000; // Use elapsed time
        TimerData timerData = new TimerData(name, endTime);

        TimerEndTask task = new TimerEndTask(name, mListener);
        Timer timer = new Timer(name);
        mTimers.put(name, timerData);
        timer.schedule(task, durationInSec * 1000);

        mListener.timeIsOn(name);

        Log.d(TAG, "Timer[" + name + "] with duration: " + durationInSec + " seconds has started. ");
        Log.d(TAG, "Current elapsed Time: "+SystemClock.elapsedRealtime()+" newStopTime: "+(SystemClock.elapsedRealtime()+durationInSec));

        return true;
    }



    public void stopTimer(final String name) {
        if (mTimers.containsKey(name)) {
            TimerData timerData = mTimers.get(name);
            timerData.getTimer().cancel();
            timerData.getTimer().purge();

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
        private final String mTimerName;
        private final TimerListener mListener;
        private boolean hasStarted = false;

        public TimerEndTask(String timerName, TimerListener listener) {
            mTimerName = timerName;
            mListener = listener;
        }

        @Override
        public void run() {
            this.hasStarted = true;
            mListener.timeIsUp(mTimerName);
        }

        public boolean hasRunStarted() {
            return this.hasStarted;
        }
    }

    private static class TimerData {
        private final String name;
        private long endTime;
        private Timer timer;

        public TimerData(String name, long endTime) {
            this.name = name;
            this.endTime = endTime;
            this.timer = new Timer(name);
        }

        public long getEndTime() {
            return endTime;
        }

        public Timer getTimer() {
            return timer;
        }
        public void setTimer(Timer timer){
            this.timer = timer;
        }
    }
}
