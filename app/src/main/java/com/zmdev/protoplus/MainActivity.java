package com.zmdev.protoplus;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;
import com.zmdev.protoplus.Utils.ThemeUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        theme.applyStyle(ThemeUtils.themeID, true);
        return theme;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Priority.PRIORITY_HIGH_ACCURACY) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "All done ! Press scan again", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error: please enable GPS to scan", Toast.LENGTH_SHORT).show();
            }
        }
    }
}