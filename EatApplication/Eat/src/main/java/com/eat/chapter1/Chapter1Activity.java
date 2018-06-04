package com.eat.chapter1;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;

import com.eat.L;
import com.eat.R;

import java.lang.ref.WeakReference;
import java.util.Random;

public class Chapter1Activity extends Activity implements Runnable {

    private static final int MSG_ACTIVATE_CONSUMER = 0;
    private static final int MSG_DEACTIVATE_CONSUMER = 1;
    private static final int MSG_DO_SOMETHING = 3;
    private static final int MSG_QUIT = 4;

    private Producer1 mProducer1;
    private Producer2 mProducer2;
    private volatile ConsumerHandler mConsumerHandler;

    private Object mReadyConsumeLock = new Object();
    private boolean mReadyConsume;
    private boolean mRunning;

    private volatile boolean mProducer1Running = false;
    private volatile boolean mProducer2Running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter1);

        final Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mRunning) {
                    b.setText("Stop");
                    startThread();
                } else {
                    b.setText("Start");
                    stopThread();
                }
            }
        });
    }

    /* --------------------------------------
     *               UI thread
     * --------------------------------------*/
    public void startThread() {
        L.i(getClass(), "TID: %d", Thread.currentThread().getId() );
        synchronized (mReadyConsumeLock) {
            if (mRunning) {
                L.d(getClass(), "Consumer is running");
                return;
            }

            mRunning = true;

            // [3] start Consumer thread
            new Thread(this, "Producer2").start();
            while (!mReadyConsume) {
                try {
                    mReadyConsumeLock.wait();
                } catch (InterruptedException ie) {
                    // ignore
                }
            }

            // [1] start Producer1 thread
            mProducer1 = new Producer1(mConsumerHandler);
            mProducer1.start();
            mProducer1Running = true;

            // [2] start Producer2 thread
            mProducer2 = new Producer2(mConsumerHandler);
            mProducer2.start();
            mProducer2Running = true;
        }
        mConsumerHandler.sendMessage(mConsumerHandler.obtainMessage(MSG_ACTIVATE_CONSUMER));
    }

    public void stopThread() {
        L.i(getClass(), "TID: %d", Thread.currentThread().getId() );
        mProducer1.stopThread();
        mProducer2.stopThread();
        synchronized (mReadyConsumeLock) {
            while (mProducer1Running || mProducer2Running) {
                try {
                    L.e(getClass(), "Wait for Producers to terminate");
                    mReadyConsumeLock.wait();
                } catch (InterruptedException ie) {
                    // ignore
                }
            }
        }
        L.e(getClass(), "Producers are all stopped!");
        mConsumerHandler.sendMessage(mConsumerHandler.obtainMessage(MSG_DEACTIVATE_CONSUMER));
        mConsumerHandler.sendMessage(mConsumerHandler.obtainMessage(MSG_QUIT));
    }


    /* --------------------------------------
     *               Producer1
     * --------------------------------------*/
    public class Producer1 extends Thread {

        private volatile ConsumerHandler mHandler;
        private boolean mIsRunning = true;

        public Producer1(ConsumerHandler handler) {
            mHandler = handler;
        }

        @Override
        public void run() {
            while (mIsRunning) {
                Random r = new Random();
                int randomInt = r.nextInt(300);
                SystemClock.sleep(randomInt);
                if (mHandler != null)
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_DO_SOMETHING, 1));
            }

            synchronized (mReadyConsumeLock) {
                mProducer1Running = false;
                mReadyConsumeLock.notifyAll();
            }
            L.w(getClass(), "> Producer 1 quit!");
        }

        public void stopThread() {
            mIsRunning = false;
        }
    }

    /* --------------------------------------
     *               Producer2
     * --------------------------------------*/
    public class Producer2 extends Thread {

        private volatile ConsumerHandler mHandler;
        private boolean mIsRunning = true;

        public Producer2(ConsumerHandler handler) {
            mHandler = handler;
        }

        @Override
        public void run() {
            while (mIsRunning) {
                Random r = new Random();
                int randomInt = r.nextInt(300);
                SystemClock.sleep(randomInt);
                if (mHandler != null)
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_DO_SOMETHING, 2));
            }

            synchronized (mReadyConsumeLock) {
                mProducer2Running = false;
                mReadyConsumeLock.notifyAll();
            }
            L.w(getClass(), "> Producer 2 quit!");
        }

        public void stopThread() {
            mIsRunning = false;
        }
    }

    /* --------------------------------------
     *               Producer2 Thread
     * --------------------------------------*/

    @Override
    public void run() {
        L.i(getClass(), "TID: %d", Thread.currentThread().getId() );
        Looper.prepare();
        synchronized (mReadyConsumeLock) {
            // make sure that ConsumerHandler is initialized.
            mConsumerHandler = new ConsumerHandler(this);
            mReadyConsume = true;
            L.e(getClass(), "Consumer thread is initialized");
            mReadyConsumeLock.notify();
        }
        Looper.loop();

        L.e(getClass(), "End of looper");

        // end of loop
        synchronized (mReadyConsumeLock) {
            mReadyConsume = mRunning = false;
            mConsumerHandler = null;
        }
    }

    private static class ConsumerHandler extends Handler {
        private WeakReference<Chapter1Activity> activity;

        public ConsumerHandler(Chapter1Activity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            Object obj = msg.obj;

            switch (what) {
                case MSG_ACTIVATE_CONSUMER:
                    L.d(getClass(), "MSG_ACTIVATE_CONSUMER");
                    // do something as thread started
                    break;
                case MSG_DEACTIVATE_CONSUMER:
                    L.d(getClass(), "MSG_DEACTIVATE_CONSUMER");
                    // do something before quitting the thread
                    break;
                case MSG_DO_SOMETHING:
                    Integer from = (Integer) obj;
                    // do something else.
                    L.i(getClass(), "MSG_DO_SOMETHING from Producer " + from);
                    break;
                case MSG_QUIT:
                    L.d(getClass(), "MSG_QUIT");
                    Looper.myLooper().quit();
                    break;
                default:
                    L.e(getClass(), "Unknown message");

            }
        }
    }
}
