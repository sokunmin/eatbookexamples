package com.eat.chapter7;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.eat.L;
import com.eat.R;


public class ThreadRetainWithFragmentActivity extends Activity {

    private static final String TAG = "ThreadRetainActivity";

    private static final String KEY_TEXT = "key_text";

    private ThreadFragment mThreadFragment;

    private TextView mTextView;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retain_thread);
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        mTextView = (TextView) findViewById(R.id.text_retain);

        FragmentManager manager = getFragmentManager();
        mThreadFragment = (ThreadFragment) manager.findFragmentByTag("threadfragment");

        if (mThreadFragment == null) {
            FragmentTransaction transaction = manager.beginTransaction();
            mThreadFragment = new ThreadFragment();
            transaction.add(mThreadFragment, "threadfragment");
            transaction.commit();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        mTextView.setText(savedInstanceState.getString(KEY_TEXT));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        outState.putString(KEY_TEXT, (String)mTextView.getText());
    }

    // Method called to start a worker thread
    public void onStartThread(View v) {
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        mThreadFragment.execute();
    }

    public void setText(final String text) {
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
                mTextView.setText(text);
            }
        });
    }
}