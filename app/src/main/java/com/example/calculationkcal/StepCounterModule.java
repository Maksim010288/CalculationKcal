package com.example.calculationkcal;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class StepCounterModule implements SensorEventListener {

    private final SensorManager sensorManager;
    private Sensor stepSensor;
    private int startSteps = -1;
    private int currentSteps = 0;
    private StepCountListener listener;

    // Інтерфейс для передачі даних в Activity або Сервіс
    public interface StepCountListener {
        void onStepCountChanged(int steps);
    }

    public StepCounterModule(Context context, StepCountListener listener) {
        this.listener = listener;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }
    }

    // Запуск відстеження
    public void startListening() {
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Log.e("StepCounterModule", "Датчик кроків не знайдено на цьому пристрої!");
        }
    }

    // Зупинка відстеження (для економії батареї)
    public void stopListening() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            // Датчик повертає загальну кількість кроків з моменту перезавантаження пристрою
            int totalStepsSinceBoot = (int) event.values[0];

            if (startSteps == -1) {
                // Фіксуємо початкове значення при запуску модуля
                startSteps = totalStepsSinceBoot;
            }

            // Рахуємо кроки за поточну сесію
            currentSteps = totalStepsSinceBoot - startSteps;

            if (listener != null) {
                listener.onStepCountChanged(currentSteps);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Зазвичай не використовується для крокоміра
    }
}
