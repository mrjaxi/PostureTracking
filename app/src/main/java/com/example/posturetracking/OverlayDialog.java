package com.example.posturetracking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class OverlayDialog extends Dialog {

    public OverlayDialog(@NonNull Context context) {
        super(context);
    }

    public OverlayDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected OverlayDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay_dialog);
    }
}