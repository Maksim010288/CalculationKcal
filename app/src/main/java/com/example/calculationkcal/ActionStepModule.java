package com.example.calculationkcal;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ActionStepModule extends AppCompatActivity implements StepCounterModule.StepCountListener {

    private int PERMISSION_REQUEST_CODE;
    private Context context;
    private Activity activity;
    private StepCounterModule stepCounterModule;
    private TextView stepsTextView;

    public ActionStepModule(Context context, Activity activity,
                            StepCounterModule stepCounterModule,
                            TextView stepsTextView, int PERMISSION_REQUEST_CODE) {
        this.context = context;
        this.activity = activity;
        this.stepCounterModule = stepCounterModule;
        this.stepsTextView = stepsTextView;
        this.PERMISSION_REQUEST_CODE = PERMISSION_REQUEST_CODE;
    }

    public void startedModule(){

        stepCounterModule = new StepCounterModule(context, this);

        // Перевірка та запит дозволів для Android 10+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSION_REQUEST_CODE);
            } else {
                stepCounterModule.startListening();
            }
        } else {
            // Для старіших версій Android дозвіл надається автоматично при встановленні
            stepCounterModule.startListening();
        }

    }

    @Override
    public void onStepCountChanged(int steps) {
        runOnUiThread(() -> stepsTextView.setText("Кроків: " + steps));
    }
}
