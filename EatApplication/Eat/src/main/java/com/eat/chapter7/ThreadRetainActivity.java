package com.eat.chapter7;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import com.eat.L;
import com.eat.R;


public class ThreadRetainActivity extends Activity {

    private static class MyThread extends Thread {
        private ThreadRetainActivity mActivity;

        public MyThread(ThreadRetainActivity activity) {
            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            mActivity = activity;
        }

        private void attach(ThreadRetainActivity activity) {
            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            mActivity = activity;
        }

        @Override
        public void run() {
            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            final String text = getTextFromNetwork();
            mActivity.setText(text);
        }

        // Long operation
        private String getTextFromNetwork() {
            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            // Simulate network operation
            SystemClock.sleep(5000);
            return "Text from network";
        }
    }

    private static MyThread myThread;
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retain_thread);
        textView = (TextView) findViewById(R.id.text_retain);
        Object retainedObject = getLastNonConfigurationInstance();
        if (retainedObject != null) {
            myThread = (MyThread) retainedObject;
            myThread.attach(this);
        }
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
    }



    @Override
    public Object onRetainNonConfigurationInstance() {
        if (myThread != null && myThread.isAlive()) {
            return myThread;
        }
        return null;
    }

    public void onStartThread(View v) {
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        myThread = new MyThread(this);
        myThread.start();
    }

    private void setText(final String text) {
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
                textView.setText(text);
            }
        });
    }
}