package com.example.calculationkcal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class StepCounterService extends Service implements SensorEventListener {

    private static final String CHANNEL_ID = "StepCounterChannel";
    private static final String PREFS_NAME = "StepCounterPrefs";
    private static final String KEY_BASE_STEPS = "base_steps";
    private static final String KEY_SAVED_STEPS = "saved_steps";

    private Context context;

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private SharedPreferences preferences;

    public void workedSensor() {
        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Створюємо обов'язкове сповіщення для Foreground Service
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Крокомір працює")
                .setContentText("Рахуємо ваші кроки у фоновому режимі...")
                // Використовуємо стандартну системну іконку Android
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        // Запуск сервісу в режимі Foreground (захищає від закриття системою)
        startForeground(1, notification);

        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }

        return START_STICKY; // Сервіс автоматично перезапуститься, якщо забракне пам'яті
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int totalStepsSinceBoot = (int) event.values[0];
            int baseSteps = preferences.getInt(KEY_BASE_STEPS, -1);

            if (baseSteps == -1) {
                // Фіксуємо початкову точку відліку при першому запуску датчика
                baseSteps = totalStepsSinceBoot;
                preferences.edit().putInt(KEY_BASE_STEPS, baseSteps).apply();
            }

            // Обчислюємо кроки за поточний період
            int calculatedSteps = totalStepsSinceBoot - baseSteps;

            // Надійно зберігаємо результат у локальну пам'ять пристрою
            preferences.edit().putInt(KEY_SAVED_STEPS, calculatedSteps).apply();

            // Відправляємо сигнал для MainActivity, щоб оновити UI в реальному часі
            Intent intent = new Intent("StepCountUpdate");
            intent.putExtra("steps", calculatedSteps);
            sendBroadcast(intent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Не використовується для крокоміра
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Крокомір Служба",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
