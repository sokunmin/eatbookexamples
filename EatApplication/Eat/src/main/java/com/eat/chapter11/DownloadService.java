package com.eat.chapter11;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.eat.L;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DownloadService extends Service {

    private ExecutorService mDownloadExecutor;
    private int mCommandCount;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadExecutor = Executors.newFixedThreadPool(4);
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDownloadExecutor.shutdownNow();
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        synchronized (this) {
            mCommandCount++;
        }
        if (intent != null) {
            downloadFile(intent.getData());
        }
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        return START_REDELIVER_INTENT;
    }

    private void downloadFile(final Uri uri) {
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        mDownloadExecutor.submit(new Runnable() {
            @Override
            public void run() {

                // Simulate long file download
                SystemClock.sleep(10000);

                synchronized (this) {
                    if (--mCommandCount <= 0) {
                        stopSelf();
                        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
                    }
                }
            }
        });
    }
}
