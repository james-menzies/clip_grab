package org.menzies.utils;

import javafx.concurrent.Task;

public class AutomaticDummyWorker extends Task<Void> {

    private boolean failTask;
    private final long totalWork;
    String title;
    private final long block = 50;


    final String SCHEDULED_MESSAGE = "%s not yet started";
    final String PROGRESS_MESSAGE = "Downloading %s...";
    final String FAILED_MESSAGE = "%s has failed";
    final String COMPLETED_MESSAGE = "%s has succeeded";
    final String CANCELLED_MESSAGE = "%s has been cancelled.";

    public AutomaticDummyWorker(String title, long duration, boolean failTask) {

        this.title = title;
        this.failTask = failTask;
        this.totalWork = duration / block;
        updateTitle(title);
        updateMessage(String.format(SCHEDULED_MESSAGE, getTitle()));
    }


    @Override
    protected Void call() throws Exception {

        updateMessage(String.format(PROGRESS_MESSAGE, title));

        if (failTask) {
            executeLoop(totalWork/2);
            String failMessage = String.format(FAILED_MESSAGE, title);
            updateMessage(failMessage);
            throw new IllegalStateException(failMessage);
        }

        else {
            executeLoop(totalWork);
            updateMessage(String.format(COMPLETED_MESSAGE, title));
        }
        return null;
    }

    private void executeLoop(long iterations) throws InterruptedException {

        for (long i = 0; i < iterations; i++) {

            updateProgress(i, totalWork);
            pause(block);
        }

    }

    private void pause(long duration) throws InterruptedException {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {

            updateMessage(String.format(CANCELLED_MESSAGE, title));
            throw new InterruptedException(e.getMessage());
        }
    }

    @Override
    protected void failed() {
        super.failed();
        System.out.println("Deleting partial file: " + title);
    }
}
