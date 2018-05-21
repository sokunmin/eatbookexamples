package com.eat.chapter9;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.eat.L;
import com.eat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;


public class InvokeActivity extends Activity {


    private static final String TAG = "InvokeActivity";

    private TextView textStatus;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoke);
        textStatus = (TextView) findViewById(R.id.text_status);
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());
    }


    public void onButtonClick(View v) {
        L.i(getClass(), "ThreadId: %d", Thread.currentThread().getId());

        SimpleExecutor simpleExecutor = new SimpleExecutor();
        simpleExecutor.execute(new Runnable() {
            @Override
            public void run() {
                L.d("SimpleExecutor / ThreadId: %d --- begin", Thread.currentThread().getId());
                List<Callable<String>> tasks = new ArrayList<Callable<String>>();
                tasks.add(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        L.d(getClass(), "[Task 1] ThreadId: %d", Thread.currentThread().getId());
                        return getFirstPartialDataFromNetwork();
                    }
                });
                tasks.add(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        L.d(getClass(), "[Task 2] ThreadId: %d", Thread.currentThread().getId());
                        return getSecondPartialDataFromNetwork();
                    }
                });

                ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(2);
                try {
                    L.w(getClass(), "ThreadId: %d, before invokeAll", Thread.currentThread().getId());
                    List<Future<String>> futures = executor.invokeAll(tasks);
                    L.w(getClass(), "ThreadId: %d, after invokeAll", Thread.currentThread().getId());

                    final String mashedData = mashupResult(futures);

                    textStatus.post(new Runnable() {
                        @Override
                        public void run() {
                            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
                            textStatus.setText(mashedData);
                        }
                    });
                    L.w(getClass(), "mashedData = %s", mashedData);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                executor.shutdown();
                L.d("SimpleExecutor / ThreadId: %d --- end", Thread.currentThread().getId());
            }
        });
    }

    private String getFirstPartialDataFromNetwork() {
        L.e(getClass(), "ThreadId: %d, task 1 started", Thread.currentThread().getId());
        SystemClock.sleep(10000);
        L.e(getClass(), "ThreadId: %d, task 1 done", Thread.currentThread().getId());
        return "MockA ";
    }

    private String getSecondPartialDataFromNetwork() {
        L.e(getClass(), "ThreadId: %d, task 2 started", Thread.currentThread().getId());
        SystemClock.sleep(2000);
        L.e(getClass(), "ThreadId: %d, task 2 done", Thread.currentThread().getId());
        return "MockB ";
    }

    private String mashupResult(List<Future<String>> futures) throws ExecutionException, InterruptedException {
        StringBuilder builder = new StringBuilder();
        for (Future<String> future : futures) {
            builder.append(future.get());
        }
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        return builder.toString();
    }
}