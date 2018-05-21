package com.eat.chapter14;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.eat.L;

import java.util.Random;

public class BasicLoader extends AsyncTaskLoader<Integer>{

    private static final String TAG = "BasicLoader";

    public BasicLoader(Context context) {
        super(context);
    }

    @Override
    protected boolean onCancelLoad() {
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        return super.onCancelLoad();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
    }

    @Override
    public Integer loadInBackground() {
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        return loadData();
    }

    private int loadData() {
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        SystemClock.sleep(3000);
        Random rand = new Random();
        int data = rand.nextInt(50);
        Log.d(TAG, "loadData - data = " + data);
        return data;
    }
}
