package com.eat.chapter11;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.eat.L;

import java.util.concurrent.Executor;


public class BoundLocalService2 extends Service {

    private final ServiceBinder mBinder = new ServiceBinder();
    private final TaskExecutor executor = new TaskExecutor();

    public interface OperationListener {
        public void onOperationDone(int i);
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public class ServiceBinder extends Binder {
        public BoundLocalService2 getService() {
            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            return BoundLocalService2.this;
        }
    }


    public int doLongSyncOperation() {
        return longOperation();
    }

    public void doLongAsyncOperation(final OperationListener listener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
                int result = longOperation();
                listener.onOperationDone(result);
            }
        });
    }

    private int longOperation() {
        SystemClock.sleep(10000);
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        return 42;
    }

    public class TaskExecutor implements Executor {
        @Override
        public void execute(Runnable runnable) {
            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            new Thread(runnable).start();
        }
    }
}
