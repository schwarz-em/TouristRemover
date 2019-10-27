package com.touristremover.frontpage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.File;


public class DisplayingPhoto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaying_photo);

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }


    private File imageProcessing(String[] imageNames) {
        return null;
    }

    private String[] getImageNames() {
        return null;
    }
}
