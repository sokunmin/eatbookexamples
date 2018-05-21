package com.eat.chapter12;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eat.L;

public class AlarmReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, NetworkCheckerIntentService.class));
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
    }
}
