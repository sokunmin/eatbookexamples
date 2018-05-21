package com.eat.chapter8;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.view.View;
import android.widget.TextView;

import com.eat.L;
import com.eat.R;


public class SharedPreferencesActivity extends Activity {

    /**
     * Show read value in a TextView.
     */
    private Handler mUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            if (msg.what == 0) {
                Integer i = (Integer) msg.obj;
                mTextValue.setText(Integer.toString(i));
            }
        }
    };

    private class SharedPreferenceThread extends HandlerThread {

        private static final String KEY = "key";
        private SharedPreferences mPrefs;
        private static final int READ = 1;
        private static final int WRITE = 2;

        private Handler mHandler;

        public SharedPreferenceThread() {
            super("SharedPreferenceThread", Process.THREAD_PRIORITY_BACKGROUND);
            mPrefs = getSharedPreferences("LocalPrefs", MODE_PRIVATE);
            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        }

        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            mHandler = new Handler(getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
                    switch (msg.what) {
                        case READ: {
                            mUiHandler.sendMessage(mUiHandler.obtainMessage(0, mPrefs.getInt(KEY, 0)));
                        }
                            break;
                        case WRITE: {
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putInt(KEY, (Integer) msg.obj);
                            editor.commit();
                        }
                            break;
                    }
                }
            };
        }

        public void read() {
            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            mHandler.sendEmptyMessage(READ);
        }

        public void write(int i) {
            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            mHandler.sendMessage(Message.obtain(Message.obtain(mHandler, WRITE, i)));
        }
    }

    private int mCount;
    private SharedPreferenceThread mThread;
    TextView mTextValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        setContentView(R.layout.activity_shared_preferences);
        mTextValue = (TextView) findViewById(R.id.text_value);
        mThread = new SharedPreferenceThread();
        mThread.start();
    }

    /**
     * Write dummy value from the UI thread.
     */
    public void onButtonClickWrite(View v) {
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        mThread.write(mCount++);
    }

    /**
     * Initiate a read from the UI thread.
     */
    public void onButtonClickRead(View v) {
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        mThread.read();
    }

    /**
     * Ensure that the background thread is terminated with the Activity.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mThread.quit();
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
    }
}
