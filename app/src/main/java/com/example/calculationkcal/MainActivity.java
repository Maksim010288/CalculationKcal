package com.example.calculationkcal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.calculationkcal.update.AppUpdater;
import com.example.calculationkcal.model.ActivityModel;
import com.example.calculationkcal.model.GoalModel;
import com.example.calculationkcal.model.MarkerModel;
import com.example.calculationkcal.model.SexHumanModel;

public class MainActivity extends AppCompatActivity {

    private final int PERMISSION_REQUEST_CODE = 100;
    private TextView weightView, heightView, ageView, stepsTextView;
    private String weight, height, age;
    private SeekBar weightSeekBar, heightSeekBar, ageSeekBar;
    private AutoCompleteTextView completeTextViewHuman,
            completeTextViewLoad, completeTextViewGoal;
    private Button calculationBut;
    private StepCounterModule stepCounterModule;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private SharedPreferences preferences;

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

        AppUpdater appUpdater = new AppUpdater(this);
        appUpdater.fetchJsonAndCheckUpdate();

        View main = findViewById(R.id.main);
        weightView = findViewById(R.id.weightSeekBarText);
        weightSeekBar = findViewById(R.id.seekBarWeight);

        heightView = findViewById(R.id.heightSeekBarText);
        heightSeekBar = findViewById(R.id.seekBarHeight);

        ageView = findViewById(R.id.ageSeekBarText);
        ageSeekBar = findViewById(R.id.seekBarAge);

        completeTextViewHuman = findViewById(R.id.autoCompleteTextViewHuman);
        completeTextViewLoad = findViewById(R.id.autoCompleteTextViewLoad);
        completeTextViewGoal = findViewById(R.id.autoCompleteTextViewGoal);

        stepsTextView = findViewById(R.id.stepTextView);

        calculationBut = findViewById(R.id.calculationButton);

        SeekBarLogic heightBarLogic = new SeekBarLogic(this, weightView, weightSeekBar);
        heightBarLogic.startLogic(main, MarkerModel.WEIGHT);

        SeekBarLogic weightBarLogic = new SeekBarLogic(this, heightView, heightSeekBar);
        weightBarLogic.startLogic(main, MarkerModel.HEIGHT);

        SeekBarLogic ageBarLogic = new SeekBarLogic(this, ageView, ageSeekBar);
        ageBarLogic.startLogic(main, MarkerModel.AGE);

        checkPermissionsAndStartService();

        calculationBut.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                weight = weightView.getText().toString().replaceAll("\\D+", "");
                height = heightView.getText().toString().replaceAll("\\D+", "");
                age = ageView.getText().toString().replaceAll("\\D+", "");

                WeightCalculation calculation = new WeightCalculation(weight, height, age, getSexHumanModel());
                Double weightCalc = calculation.startWeightCalculation(getActivityModel(), getGoalModel());
                String weightStr = String.valueOf(weightCalc);
                int resultWeight = (int) Double.parseDouble(weightStr);
                calculationBut.setText(resultWeight + " Kcal");
            }
        });

        SelectedList selectedList = new SelectedList(this, main);
        selectedList.getHuman();
        selectedList.getLoad();
        selectedList.getGoal();
    }

    private SexHumanModel getSexHumanModel() {
        switch (completeTextViewHuman.getText().toString()) {
            case "ЧОЛОВІК":
                return SexHumanModel.MAN;
            case "ЖІНКА":
                return SexHumanModel.WOMAN;
        }
        return null;
    }

    private ActivityModel getActivityModel() {
        switch (completeTextViewLoad.getText().toString()) {
            case "СИДЯЧИЙ СПОСІБ ЖИТТЯ":
                return ActivityModel.SEDENTARY;
            case "ЛЕГКА":
                return ActivityModel.LIGHT_ACTIVITY;
            case "ПОМІРНА":
                return ActivityModel.MODERATE_ACTIVITY;
            case "ВИСОКА":
                return ActivityModel.HIGH_ACTIVITY;
            case "ДУЖЕ ВИСОКА":
                return ActivityModel.VERY_HIGH_ACTIVITY;
        }
        return null;
    }

    private GoalModel getGoalModel() {
        switch (completeTextViewGoal.getText().toString()) {
            case "ВТРИМАТИ ВАГУ":
                return GoalModel.SUPPORT;
            case "СКИНУТИ ВАГУ":
                return GoalModel.TO_LOSE;
            case "НАБРАТИ ВАГУ":
                return GoalModel.TO_GAIN;
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                stepCounterModule.startListening();
            } else {
                Toast.makeText(this, "Дозвіл відхилено! Шагомір не працюватиме.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkPermissionsAndStartService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 100);
            } else {
                startStepService();
            }
        } else {
            startStepService();
        }
    }


    private void startStepService() {
        Intent serviceIntent = new Intent(this, StepCounterService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}