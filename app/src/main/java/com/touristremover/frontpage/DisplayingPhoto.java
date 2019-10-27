package com.touristremover.frontpage;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DisplayingPhoto extends AppCompatActivity {
    protected Python py;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaying_photo);

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        System.out.println(getImageNames());
    }


    public void imageProcessing(String[] imageNames) {
        PyObject pyDocument = py.getModule("PythonImageMatch.py");
        PyObject image = pyDocument.callAttr("stich_multiple_image", imageNames);
        byte[] data = image.toJava(byte[].class);
        saveImage(data);

    }

    public void saveImage(byte[] data) {
        final String TAG = "saving image";
        Log.d(TAG, "media file creating process started");
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions");
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        return mediaFile;
    }

    private String[] getImageNames() {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if(directory.exists()) {
            File[] allFiles = directory.listFiles();
            String[] files = new String[allFiles.length];
            for(int i = 0; i < files.length; i++) {
                files[i] = allFiles[i].getAbsolutePath();
            }
            return files;
        }
        Log.d("getImageNames","Selected directory does not exist");
        return null;
    }
}
