package com.example.feedler;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AppExecutors {

    private final Executor postDatabaseExrcutor;

    private final ExecutorService favoriteDatabaseExrcutor;

    private final Executor mMainThread;

    private final ScheduledExecutorService mScheduledExecutorService;

    private static AppExecutors sAppExecutors = new AppExecutors();

    public static AppExecutors getInstance() {
        return sAppExecutors;
    }

    private AppExecutors(Executor postDatabaseExrcutor, ExecutorService favoriteDatabaseExrcutor, Executor mainThread, ScheduledExecutorService scheduledExecutorService) {
        this.postDatabaseExrcutor = postDatabaseExrcutor;
        this.favoriteDatabaseExrcutor = favoriteDatabaseExrcutor;
        this.mMainThread = mainThread;
        mScheduledExecutorService = scheduledExecutorService;
    }

    private AppExecutors() {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3),
                new MainThreadExecutor(), Executors.newScheduledThreadPool(1));
    }

    public Executor postDatabaseExrcutor() {
        return postDatabaseExrcutor;
    }

    public ExecutorService favoriteDatabaseExrcutor() {
        return favoriteDatabaseExrcutor;
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
