package com.example.myupdateinstaler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

public class UpdateInstaller {

    public static void installApk(Context context, File apkFile) {
//        if (apkFile == null || !apkFile.exists()) {
//            return;
//        }

        Intent intent = new Intent(Intent.ACTION_VIEW);

        // Важливо: Очищуємо тип та дані перед встановленням нових, щоб уникнути багів кешу Intent
        intent.setDataAndType(null, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileprovider",
                    apkFile
            );

            // Обов'язково додаємо прапорці доступу до URI
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION); // Додатковий дозвіл для нових Android
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }

        // Ці три прапорці разом змушують систему відкривати Activity як новий незалежний процес інсталяції
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
