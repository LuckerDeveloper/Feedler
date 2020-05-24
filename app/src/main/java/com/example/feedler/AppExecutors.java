package com.example.feedler;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AppExecutors {

    private final Executor postDatabaseExecutor;

    private final ExecutorService ImageDatabaseExecutor;

    private final Executor mMainThread;

    private final ScheduledExecutorService mScheduledExecutorService;

    private static AppExecutors sAppExecutors = new AppExecutors();

    public static AppExecutors getInstance() {
        return sAppExecutors;
    }

    private AppExecutors(Executor postDatabaseExecutor, ExecutorService ImageDatabaseExecutor, Executor mainThread, ScheduledExecutorService scheduledExecutorService) {
        this.postDatabaseExecutor = postDatabaseExecutor;
        this.ImageDatabaseExecutor = ImageDatabaseExecutor;
        this.mMainThread = mainThread;
        mScheduledExecutorService = scheduledExecutorService;
    }

    private AppExecutors() {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3),
                new MainThreadExecutor(), Executors.newScheduledThreadPool(1));
    }

    public Executor postDatabaseExecutor() {
        return postDatabaseExecutor;
    }

    public ExecutorService ImageDatabaseExecutor() {
        return ImageDatabaseExecutor;
    }

    public Executor mainThread() {
        return mMainThread;
    }

    public ScheduledExecutorService scheduled(){
        return mScheduledExecutorService;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
