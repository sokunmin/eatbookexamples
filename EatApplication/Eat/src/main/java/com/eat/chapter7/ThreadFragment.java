package com.eat.chapter7;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;

import com.eat.L;

public class ThreadFragment extends Fragment {

    private ThreadRetainWithFragmentActivity mActivity;
    private MyThread myThread;

    private class MyThread extends Thread {

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ThreadRetainWithFragmentActivity) activity;
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
    }

    public void execute() {
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        myThread = new MyThread();
        myThread.start();
    }
}
