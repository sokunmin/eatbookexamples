package com.eat.chapter4;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.eat.L;
import com.eat.R;


public class HandlerCallbackActivity extends Activity implements Handler.Callback {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_callback);
        L.d(getClass(), "UIThread / ThreadId: %d", Thread.currentThread().getId());

    }

    @Override
    public boolean handleMessage(Message msg) {
        L.i(getClass(), "[1] ThreadId: %d", Thread.currentThread().getId());

        switch (msg.what) {
            case 1:
                msg.what = 11;
                L.i(getClass(), "[2] ThreadId: %d", Thread.currentThread().getId());
                return true;
            default:
                msg.what = 22;
                L.i(getClass(), "[3] ThreadId: %d", Thread.currentThread().getId());
                return false;
        }
    }

    public void onHandlerCallback(View v) {
        Handler handler = new Handler(this) {
            @Override
            public void handleMessage(Message msg) {
                // Process message
                L.i(getClass(), "Handler / ThreadId: %d", Thread.currentThread().getId());
            }
        };
        handler.sendEmptyMessage(1);
        handler.sendEmptyMessage(2);
    }
}
