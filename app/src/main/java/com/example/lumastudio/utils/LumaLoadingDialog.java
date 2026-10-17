package com.example.lumastudio.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;

import com.example.lumastudio.R;

public class LumaLoadingDialog {

    private final Dialog dialog;
    private final TextView tvMessage;

    public LumaLoadingDialog(Context context, String message) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_luma_loading);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        tvMessage = dialog.findViewById(R.id.tv_loading_message);
        if (message != null && !message.isEmpty()) {
            tvMessage.setText(message);
        }
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}