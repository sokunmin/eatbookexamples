package com.eat.chapter9;


import android.util.Log;

import com.eat.L;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskTrackingThreadPool extends ThreadPoolExecutor {

    private static final String TAG = "CustomThreadPool";

    private AtomicInteger mTaskCount = new AtomicInteger(0);

    public TaskTrackingThreadPool() {
        super(3, 3, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        L.d(getClass(), "ThreadId: %d, beforeExecute - thread = %s", Thread.currentThread().getId(), t.getName());
        mTaskCount.getAndIncrement();
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        L.d(getClass(), "ThreadId: %d, afterExecute - thread = %s", Thread.currentThread().getId(), t);
        mTaskCount.getAndDecrement();
    }

    @Override
    protected void terminated() {
        super.terminated();
        Log.d(TAG, "terminated - thread = " + Thread.currentThread().getName());
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
    }

    public int getNbrOfTasks() {
        return mTaskCount.get();
    }
}
