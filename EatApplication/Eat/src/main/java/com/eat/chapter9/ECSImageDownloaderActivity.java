package com.eat.chapter9;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.eat.L;
import com.eat.R;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class ECSImageDownloaderActivity extends Activity {

    private static final String TAG = "ECSImageDownloaderActivity";

    private LinearLayout layoutImages;

    private class ImageDownloadTask implements Callable<Bitmap> {

        private int id = -1;
        public ImageDownloadTask(int id) {
            this.id = id;
        }

        @Override
        public Bitmap call() throws Exception {
            return downloadRemoteImage();
        }

        private Bitmap downloadRemoteImage() {
            SystemClock.sleep((int) (5000f - 1* 5000f));
//            SystemClock.sleep((int) (5000f - new Random().nextFloat() * 5000f));
            L.d(getClass(), "call() -> ThreadId: %d, task id = %d", Thread.currentThread().getId(), this.id);
            return BitmapFactory.decodeResource(ECSImageDownloaderActivity.this.getResources(), R.drawable.ic_launcher);
        }
    }

    private class DownloadCompletionService extends ExecutorCompletionService {

        private ExecutorService mExecutor;

        public DownloadCompletionService(ExecutorService executor) {
            super(executor);
            mExecutor = executor;
            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        }

        public void shutdown() {
            mExecutor.shutdown();
            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        }

        public boolean isTerminated() {
//            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            return mExecutor.isTerminated();
        }
    }

    private class ConsumerThread extends Thread {

        private DownloadCompletionService mEcs;

        private ConsumerThread(DownloadCompletionService ecs) {
            L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            this.mEcs = ecs;
        }

        @Override
        public void run() {
            L.w(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            super.run();
            try {
                while(!mEcs.isTerminated()) {
                    Future<Bitmap> future = mEcs.poll(1, TimeUnit.SECONDS);
                    if (future != null) {
                        L.w(getClass(), "> in loop > ThreadId: %d", Thread.currentThread().getId());
                        addImage(future.get());
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.i(getClass(), "ThreadId: %d ----- begin", Thread.currentThread().getId());
        setContentView(R.layout.activity_ecs_image_downloader);
        layoutImages = (LinearLayout) findViewById(R.id.layout_images);

        DownloadCompletionService ecs = new DownloadCompletionService(Executors.newCachedThreadPool());
        new ConsumerThread(ecs).start();

        for (int i = 0; i < 5; i++) {
            ecs.submit(new ImageDownloadTask(i));
        }

        ecs.shutdown();
        L.i(getClass(), "ThreadId: %d ----- end", Thread.currentThread().getId());
    }


    private void addImage(final Bitmap image) {
        L.w(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView iv = new ImageView(ECSImageDownloaderActivity.this);
                iv.setImageBitmap(image);
                layoutImages.addView(iv);
                L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            }
        });
    }
}