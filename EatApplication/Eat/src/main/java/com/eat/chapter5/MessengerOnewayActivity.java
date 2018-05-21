package com.eat.chapter5;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;

import com.eat.L;
import com.eat.R;

import java.util.List;


/**
 * Activity passing messages to remote service via Messenger.
 */
public class MessengerOnewayActivity extends Activity {

    private boolean mBound = false;
    private Messenger mRemoteService = null;

    private ServiceConnection mRemoteConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            mRemoteService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            mRemoteService = null;
            mBound = false;
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger_client);
        L.i(getClass());
    }

    public void onBindClick(View v) {
        L.i(getClass());
        // [1] explicitly indicate service to the intent.
//        Intent intent = new Intent(this, WorkerThreadService.class);

        // [2] convert implicit intent to explicit intent.
//        Intent action = new Intent("com.eat.chapter5.ACTION_BIND");
//        Intent intent = new Intent(createExplicitFromImplicitIntent(this, action));

        // [3] bind by action filter with specific action
        Intent intent = new Intent("com.eat.chapter5.ACTION_BIND");
        intent.setPackage(getPackageName());
        bindService(intent, mRemoteConnection, Context.BIND_AUTO_CREATE);
    }

    public void onUnbindClick(View v) {
        if (mBound) {
            unbindService(mRemoteConnection);
            mBound = false;
        }
    }

    public void onSendClick(View v) {
        if (mBound) {
            try {
                mRemoteService.send(Message.obtain(null, 2, 0, 0));
            } catch (RemoteException e) {
                // Empty
            }
        }
    }

    /***
     * Android L (lollipop, API 21) introduced a new problem when trying to invoke implicit intent,
     * "java.lang.IllegalArgumentException: Service Intent must be explicit"
     *
     * If you are using an implicit intent, and know only 1 target would answer this intent,
     * This method will help you turn the implicit intent into the explicit form.
     *
     * Inspired from SO answer: http://stackoverflow.com/a/26318757/1446466
     * @param context
     * @param implicitIntent - The original implicit intent
     * @return Explicit Intent created from the implicit original intent
     */
    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }
}