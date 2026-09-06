package com.example.calculationkcal.update;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;

public class UpdateInstaller {
    public static void installApk1(Context context, File apkFile) {
        if (!apkFile.exists()) return;

        // Формуємо безпечний URI через FileProvider
        String authority = context.getPackageName() + ".fileprovider";
        Uri apkUri = FileProvider.getUriForFile(context, authority, apkFile);

        // Створюємо інтент для відкриття системного установника пакетів
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");


        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Надаємо права на читання цього файлу для інсталятора
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(intent);
    }

    public static void installApk(Context context, File apkFile) {
        if (apkFile == null || !apkFile.exists()) {
            // Лог або повідомлення, що файл не знайдено
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);

        // Перевірка версії Android (для Android 7.0 Nougat / API 24 і вище потрібен FileProvider)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileprovider",
                    apkFile
            );
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            // Для старих версій Android (менше API 24)
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }

        // Прапорець обов'язковий, якщо метод викликається не з Activity (наприклад, із Service або BroadcastReceiver)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Запуск інсталятора
        context.startActivity(intent);
    }
}
