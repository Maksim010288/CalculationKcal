package com.example.calculationkcal.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppUpdater {

    private final Context context;
    private long downloadId;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final String JSON_URL = "https://raw.githubusercontent.com/Maksim010288/CalculationKcal/master/app/update.json";


    public AppUpdater(Context context) {
        this.context = context;
    }

    // 1. ПЕРЕВІРКА: Порівнюємо поточну версію з версією на сервере
    private void checkAndDownloadUpdate(int serverVersionCode, String apkUrl) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            long currentVersionCode = pInfo.versionCode; // Поточна версія додатка

            if (serverVersionCode > currentVersionCode) {
                Log.i("jsonformat", String.valueOf("виконано"));
                // Якщо на сервері версія новіша — запускаємо завантаження
                startDownloading(apkUrl);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 2. ЗАВАНТАЖЕННЯ: Використовуємо системний DownloadManager
    private void startDownloading(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        request.setTitle("Оновлення додатка");
        request.setDescription("Завантаження нової версії...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // Зберігаємо файл у зовнішній кеш додатка (не потребує дозволів на запис пам'яті)
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "update.apk");

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            // Реєструємо приймач, який спрацює, коли завантаження завершиться
            // context.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(onDownloadComplete,
                        new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                        Context.RECEIVER_EXPORTED);
            } else {
                context.registerReceiver(onDownloadComplete,
                        new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }

            downloadId = manager.enqueue(request); // Запуск завантаження
        }
    }

    // 3. ПЕРЕХОПЛЕННЯ: Чекаємо фінішу завантаження та викликаємо інсталятор
    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (downloadId == id) {
                // Знімаємо реєстрацію приймача, щоб уникнути витоку пам'яті
                context.unregisterReceiver(this);

                // Знаходимо завантажений файл за вказаним раніше шляхом
                File apkFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "update.apk");

             //   if (apkFile.exists()) {
                    // Викликаємо метод встановлення, який ми розбирали раніше
                    UpdateInstaller.installApk(context, apkFile);
                //}
            }
        }
    };

    public void fetchJsonAndCheckUpdate() {
        executor.execute(() -> {
            try {
                URL url = new URL(JSON_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                // Читаємо відповідь сервера
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Парсимо JSON
                JSONObject jsonObject = new JSONObject(response.toString());
                int serverVersionCode = jsonObject.getInt("versionCode");
                String apkUrl = jsonObject.getString("downloadUrl");


                // Повертаємося в головний потік для виклику вашої логіки перевірки
                mainHandler.post(() -> checkAndDownloadUpdate(serverVersionCode, apkUrl));


            } catch (Exception e) {
                e.printStackTrace();
                // Тут можна додати лог або callback про помилку мережі
            }
        });
    }
}
