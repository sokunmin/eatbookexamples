package com.eat.chapter14;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.eat.L;
import com.eat.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FileActivity extends Activity {

    private static final String TAG = "FileActivity";

    private static int mCount; // A count to append to file names

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
    }

    public void onAddFile(View v) {
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        String filename = "testfile" + mCount++ + ".txt";
        File file = new File(this.getFilesDir(), filename);
        OutputStream out;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            out.write("Test".getBytes("UTF-8"));
            out.close();
        } catch (Exception e){

        }
        Log.d(TAG, "onAddFile - path = " + file.getAbsolutePath());

    }

    public void onRemoveFiles(View v) {
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        removeAllFiles();
    }

    private void removeAllFiles() {
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        File dir = getFilesDir();
        File[] files = dir.listFiles();
        for (File file : files) {
            file.delete();
        }
    }
}