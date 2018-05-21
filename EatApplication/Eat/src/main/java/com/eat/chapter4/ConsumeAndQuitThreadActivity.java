package com.eat.chapter4;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.SystemClock;

import com.eat.L;

import java.util.Random;


public class ConsumeAndQuitThreadActivity extends Activity {


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ConsumeAndQuitThread consumeAndQuitThread = new ConsumeAndQuitThread();
        consumeAndQuitThread.start();
        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    L.i(getClass(), "Thread %d / ThreadId: %d", finalI, Thread.currentThread().getId());
                    for (int i = 0; i < 10; i++) {
                        SystemClock.sleep(new Random().nextInt(10));
                        consumeAndQuitThread.enqueueData(i);
                    }
                }
            }).start();
        }
    }

    private static class ConsumeAndQuitThread extends Thread implements MessageQueue.IdleHandler {

        private static final String THREAD_NAME = "ConsumeAndQuitThread";

        public Handler mConsumerHandler;
        private boolean mIsFirstIdle = true;

        public ConsumeAndQuitThread() {
            super(THREAD_NAME);
        }

        @Override
        public void run() {
            L.i(getClass(), "[1] Handler / ThreadId: %d", Thread.currentThread().getId());
            Looper.prepare();

            mConsumerHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // Consume data
                    L.i(getClass(), "[2] Handler / ThreadId: %d", Thread.currentThread().getId());
                }
            };
            Looper.myQueue().addIdleHandler(this);
            Looper.loop();
        }


        @Override
        public boolean queueIdle() {
            L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            if (mIsFirstIdle) {
                mIsFirstIdle = false;
                return true;
            }
            mConsumerHandler.getLooper().quit();
            return false;
        }

        public void enqueueData(int i) {
            L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            mConsumerHandler.sendEmptyMessage(i);
        }
    }
}
