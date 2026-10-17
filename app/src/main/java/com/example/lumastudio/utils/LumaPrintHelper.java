package com.example.lumastudio.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;
import androidx.print.PrintHelper;

public class LumaPrintHelper {

    public static void printBitmap(Context context, Bitmap bitmap, String jobName) {
        if (bitmap == null) {
            Toast.makeText(context, "Tidak ada gambar untuk dicetak", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            PrintHelper printHelper = new PrintHelper(context);
            printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            printHelper.printBitmap(jobName != null ? jobName : "Luma Photobooth Print", bitmap);
            Toast.makeText(context, "Membuka dialog pencetakan...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Gagal membuka layanan pencetak: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}