package com.example.calculationkcal.update;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;

public class UpdateInstaller {
    public static void installApk(Context context, File apkFile) {
        if (!apkFile.exists()) return;

        // Формуємо безпечний URI через FileProvider
        String authority = context.getPackageName() + ".fileprovider";
        Uri apkUri = FileProvider.getUriForFile(context, authority, apkFile);

        // Створюємо інтент для відкриття системного установника пакетів
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");

        // Надаємо права на читання цього файлу для інсталятора
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(intent);
    }
}
