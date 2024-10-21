package com.example.stoptimer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NRJ_"+MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startTimer();
    }

    private void startTimer() {
        TimerManager timerManager = new TimerManager(new TimerManager.TimerListener() {
            @SuppressLint("NewApi")
            @Override
            public void timeIsOn(String timerName) {
                @SuppressLint({"NewApi", "LocalSuppress"}) LocalDateTime now = LocalDateTime.now();
                @SuppressLint({"NewApi", "LocalSuppress"}) DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                Log.d(TAG, "timerManager timeIsOn : "+now.format(formatter));
            }

            @SuppressLint("NewApi")
            @Override
            public void timeIsUp(String timerName) {
                @SuppressLint({"NewApi", "LocalSuppress"}) LocalDateTime now = LocalDateTime.now();
                @SuppressLint({"NewApi", "LocalSuppress"}) DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                Log.d(TAG, "timerManager timeIsUp : "+now.format(formatter));
            }

            @Override
            public void timeIsOff(String timerName) {
                Log.d(TAG, "timerManager timeIsOff");
            }
        });

//        timerManager.setTimer("Timer-10-secs", 100);

        TimerManager2 timerManager2 = new TimerManager2(new TimerManager2.TimerListener() {
            @SuppressLint("NewApi")
            @Override
            public void timeIsOn(String timerName) {
                @SuppressLint({"NewApi", "LocalSuppress"}) LocalDateTime now = LocalDateTime.now();
                @SuppressLint({"NewApi", "LocalSuppress"}) DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                Log.d(TAG, "timerManager2 timeIsOn : "+now.format(formatter));
            }

            @SuppressLint("NewApi")
            @Override
            public void timeIsUp(String timerName) {
                @SuppressLint({"NewApi", "LocalSuppress"}) LocalDateTime now = LocalDateTime.now();
                @SuppressLint({"NewApi", "LocalSuppress"}) DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                Log.d(TAG, "timerManager2 timeIsUp : "+now.format(formatter)+", "+SystemClock.elapsedRealtime());
            }

            @Override
            public void timeIsOff(String timerName) {
                Log.d(TAG, "timerManager2 timeIsOff");
            }
        }, this);
        timerManager2.setTimer("Timer-10-secs", 50);
    }
}