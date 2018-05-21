package com.eat.chapter11;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.eat.L;


public class BoundLocalActivity extends Activity {

    private LocalServiceConnection mLocalServiceConnection = new LocalServiceConnection();
    private BoundLocalService mBoundLocalService;
    private boolean mIsBound;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindService(new Intent(BoundLocalActivity.this, BoundLocalService.class),
                mLocalServiceConnection,
                Service.BIND_AUTO_CREATE);
        mIsBound = true;
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsBound) {
            try {
                unbindService(mLocalServiceConnection);
                mIsBound = false;
                L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            } catch (IllegalArgumentException e) {
                // No bound service
            }
        }
    }

    private class LocalServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBoundLocalService = ((BoundLocalService.ServiceBinder) iBinder).getService();
            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());

            // At this point clients can invoke methods in the Service,
            // i.e. publishedMethod1 and publishedMethod2.
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBoundLocalService = null;
            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        }
    }
}
