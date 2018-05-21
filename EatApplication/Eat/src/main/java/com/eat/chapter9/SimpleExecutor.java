package com.eat.chapter9;

import com.eat.L;

import java.util.concurrent.Executor;

public class SimpleExecutor implements Executor {
    @Override
    public void execute(Runnable runnable) {
        new Thread(runnable).start();
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
    }
}
