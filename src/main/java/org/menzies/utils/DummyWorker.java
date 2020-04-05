package org.menzies.utils;

import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;

import java.util.concurrent.CountDownLatch;

public class DummyWorker extends Task<Void> {

    private boolean failTask;
    private final long totalWork;


    final String SCHEDULED_MESSAGE = "%s not yet started";
    final String PROGRESS_MESSAGE = "%s in progress";
    final String FAILED_MESSAGE = "%s has failed";
    final String COMPLETED_MESSAGE = "%s has succeeded";

    public DummyWorker(String title, long duration, boolean failTask) {

        this.failTask = failTask;
        this.totalWork = duration / 100;
        updateTitle(title);
        pause(2);
        updateMessage(String.format(SCHEDULED_MESSAGE, getTitle()));
    }


    @Override
    protected Void call() throws Exception {

        if (failTask) {
            executeLoop(totalWork/2);
            String failMessage = String.format(FAILED_MESSAGE, getTitle());
            updateMessage(failMessage);
            throw new IllegalStateException(failMessage);
        }

        else {
            executeLoop(totalWork);
            updateMessage(String.format(COMPLETED_MESSAGE, getTitle()));
        }
        return null;
    }


    private void executeLoop(long iterations) {

        for (long i = 0; i < iterations; i++) {

            updateProgress(i, totalWork);
            pause(100);
        }

    }

    private void pause(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
