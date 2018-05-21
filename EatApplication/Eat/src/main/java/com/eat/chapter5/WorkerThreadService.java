package com.eat.chapter5;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.eat.L;


public class WorkerThreadService extends Service {

    private static final String TAG = "WorkerThreadService";
    WorkerThread mWorkerThread;
    Messenger mWorkerMessenger;

    @Override
    public void onCreate() {
        super.onCreate();
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        mWorkerThread = new WorkerThread();
        mWorkerThread.start();
    }

    // Worker thread has prepared a looper and handler.
    private void onWorkerPrepared() {
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        mWorkerMessenger = new Messenger(mWorkerThread.mWorkerHandler);
        synchronized(this) {
            notifyAll();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        synchronized (this) {
            while (mWorkerMessenger == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // Empty
                }
            }
        }
        return mWorkerMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        mWorkerThread.quit();
    }

    private class WorkerThread extends Thread {

        Handler mWorkerHandler;

        @Override
        public void run() {
            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            Looper.prepare();
            mWorkerHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
                    switch (msg.what) {
                        case 1:
                            try {
                                msg.replyTo.send(Message.obtain(null, msg.what, 0, 0));
                            } catch (RemoteException e) {
                                L.e(TAG, e.getMessage());
                            }
                            break;
                        case 2:
                            L.d(getClass(), "Message received");
                            break;
                    }

                }
            };
            onWorkerPrepared();
            Looper.loop();
        }

        public void quit() {
            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            mWorkerHandler.getLooper().quit();
        }
    }
}
