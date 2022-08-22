package com.example.posturetracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.res.Configuration;
import android.os.Bundle;

public class DialogAddItem extends AppCompatActivity {

    private ConstraintLayout layout;

    public static int newOrientation;

    public static int getNewOrientation() {
        return newOrientation;
    }

    public static void setNewOrientation(int newOrientation) {
        DialogAddItem.newOrientation = newOrientation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_add_item);

        layout = findViewById(R.id.overlay_view);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        newOrientation = newConfig.orientation;

        if (newOrientation == Configuration.ORIENTATION_PORTRAIT) {
            layout.setRotation(0);
             setNewOrientation(Configuration.ORIENTATION_PORTRAIT);
        } else {
            layout.setRotation(-90.f);
            setNewOrientation(Configuration.ORIENTATION_LANDSCAPE);
        }
    }
}