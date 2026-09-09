package com.example.calculationkcal;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

public class LogHelper {

    private static final String TAG = "LogHelper";
    private static final String FOLDER_NAME = "CalculationKcal_Logs";
    private static final String FILE_NAME = "app_log.txt";

    /**
     * Метод для запису логу у файл.
     * @param context Контекст додатка
     * @param message Текст, який потрібно зафіксувати
     */
    public static void writeLog(Context context, String message) {
        // Отримуємо шлях до стандартної папки "Документи" на телефоні
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        // Створюємо нашу підпапку для логів додатка
        File appLogDir = new File(documentsDir, FOLDER_NAME);

        if (!appLogDir.exists()) {
            boolean isCreated = appLogDir.mkdirs();
            if (!isCreated) {
                Log.e(TAG, "Не вдалося створити папку для логів");
                return;
            }
        }

        // Цільовий файл логу
        File logFile = new File(appLogDir, FILE_NAME);

        // Форматуємо поточний час (наприклад: 2026-09-09 23:15:00)
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = formatter.format(new Date());

        // Формуємо фінальний рядок логу
        String logLine = "[" + timestamp + "] " + message + "\n";

        FileWriter writer = null;
        try {
            // Другий параметр "true" означає, що дані будуть додаватися в кінець файлу, а не перезаписувати його
            writer = new FileWriter(logFile, true);
            writer.append(logLine);
            writer.flush();
            Log.d(TAG, "Лог успішно записано: " + logLine.trim());
        } catch (IOException e) {
            Log.e(TAG, "Помилка запису логу у файл", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    Log.e(TAG, "Не вдалося закрити FileWriter", e);
                }
            }
        }
    }
}
