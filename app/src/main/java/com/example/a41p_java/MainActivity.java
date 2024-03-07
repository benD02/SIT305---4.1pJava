package com.example.a41p_java;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText workoutDurationEditText;
    private EditText restDurationEditText;
    private TextView workoutTimeTextView;
    private TextView restTimeTextView;
    private ProgressBar workoutProgressBar;
    private Button startButton;
    private Button stopButton;

    private CountDownTimer workoutTimer;
    private CountDownTimer restTimer;
    private Phase currentPhase = Phase.WORKOUT;

    private long workoutDurationInMillis = 0;
    private long restDurationInMillis = 0;

    private final Handler handler = new Handler();
    private final Runnable updateUIRunnable = new Runnable() {
        @Override
        public void run() {
            updateUI();
            handler.postDelayed(this, 1000); // Update every second
        }
    };

    private enum Phase {
        WORKOUT,
        REST
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        workoutDurationEditText = findViewById(R.id.workoutDurationEditText);
        restDurationEditText = findViewById(R.id.restDurationEditText);
        workoutTimeTextView = findViewById(R.id.workoutTimeTextView);
        restTimeTextView = findViewById(R.id.restTimeTextView);
        workoutProgressBar = findViewById(R.id.workoutProgressBar);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWorkout();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopWorkout();
            }
        });
    }

    private void startWorkout() {
        String workoutDurationText = workoutDurationEditText.getText().toString();
        String restDurationText = restDurationEditText.getText().toString();

        if (workoutDurationText.isEmpty() || restDurationText.isEmpty()) {
            // Handle empty input
            return;
        }

        long workoutDuration = Long.parseLong(workoutDurationText);
        long restDuration = Long.parseLong(restDurationText);

        workoutDurationInMillis = workoutDuration * 60000; // Convert minutes to milliseconds
        restDurationInMillis = restDuration * 60000;

        startButton.setEnabled(false);
        stopButton.setEnabled(true);

        startWorkoutTimer();
    }

    private void startWorkoutTimer() {
        workoutTimer = new CountDownTimer(workoutDurationInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
                workoutTimeTextView.setText("Workout Time Remaining: " + timeLeftFormatted);

                int progress = (int) ((millisUntilFinished * 100) / workoutDurationInMillis);
                workoutProgressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                workoutTimeTextView.setText("Workout Time Remaining: 00:00");
                workoutProgressBar.setProgress(0);
                playSound(); // Play sound when workout finishes
                startRestTimer();
            }
        }.start();

        handler.post(updateUIRunnable);
    }

    private void startRestTimer() {
        currentPhase = Phase.REST;
        restTimer = new CountDownTimer(restDurationInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
                restTimeTextView.setText("Rest Time Remaining: " + timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                restTimeTextView.setText("Rest Time Remaining: 00:00");
                playSound(); // Play sound when rest finishes
                startWorkoutTimer();
            }
        }.start();
    }

    private void stopWorkout() {
        if (workoutTimer != null) {
            workoutTimer.cancel();
        }
        if (restTimer != null) {
            restTimer.cancel();
        }

        handler.removeCallbacks(updateUIRunnable);

        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    private void updateUI() {
        switch (currentPhase) {
            case WORKOUT:
                workoutTimeTextView.setVisibility(View.VISIBLE);
                workoutProgressBar.setVisibility(View.VISIBLE);
                restTimeTextView.setVisibility(View.GONE);
                break;
            case REST:
                workoutTimeTextView.setVisibility(View.GONE);
                workoutProgressBar.setVisibility(View.GONE);
                restTimeTextView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void playSound() {
        // Play sound or vibrate here
    }
}
