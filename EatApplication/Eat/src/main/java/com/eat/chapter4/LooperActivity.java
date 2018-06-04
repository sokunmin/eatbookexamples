package com.eat.chapter4;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.eat.L;
import com.eat.R;


public class LooperActivity extends Activity {

    LooperThread mLooperThread;

    private static class LooperThread extends Thread {

        public Handler mHandler;

        @SuppressLint("HandlerLeak")
        public void run() {

            L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());

            Looper.prepare();
            mHandler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        doLongRunningOperation();
                    }
                }
            };
            Looper.loop();
        }

        private void doLongRunningOperation() {
            // Add long running operation here.
            L.i(getClass(), "Handler / ThreadId: %d", Thread.currentThread().getId());
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looper);

        mLooperThread = new LooperThread();
        mLooperThread.start();
    }

    public void onClick(View v) {
        if (mLooperThread.mHandler != null) {
            Message msg = mLooperThread.mHandler.obtainMessage(1);
            mLooperThread.mHandler.sendMessage(msg);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        mLooperThread.mHandler.getLooper().quit();
    }
}
