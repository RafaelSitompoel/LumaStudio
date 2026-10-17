package com.example.lumastudio.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;

public class PrintExportManager {

    private final StorageManager storageManager;

    public PrintExportManager(Context context) {
        this.storageManager = new StorageManager(context);
    }

    public Uri exportAsJpeg(Bitmap bitmap, int quality) {
        try {
            File file = storageManager.createTempFile("LUMA_PRINT_", ".jpg");
            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            }
            return storageManager.saveBitmap(bitmap, "LUMA_JPEG");
        } catch (Exception e) {
            return null;
        }
    }

    public Uri exportAsPng(Bitmap bitmap) {
        try {
            File file = storageManager.createTempFile("LUMA_PRINT_", ".png");
            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            return storageManager.saveBitmap(bitmap, "LUMA_PNG");
        } catch (Exception e) {
            return null;
        }
    }

    public File exportAsPdf(Bitmap bitmap) {
        try {
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            page.getCanvas().drawBitmap(bitmap, 0, 0, null);
            pdfDocument.finishPage(page);

            File pdfFile = storageManager.createTempFile("LUMA_PRINT_", ".pdf");
            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                pdfDocument.writeTo(fos);
            }
            pdfDocument.close();
            return pdfFile;
        } catch (Exception e) {
            return null;
        }
    }
}