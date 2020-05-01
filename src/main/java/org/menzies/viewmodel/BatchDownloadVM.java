package org.menzies.viewmodel;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.util.Callback;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

public class BatchDownloadVM<T extends Task<?>> {

    private final ExecutorService service;
    private final Iterator<T> pendingTasks;
    private final ObservableList<DownloadTileVM> runningViewModels;
    private final Map<T, DownloadTileVM> taskToVMReference;

    private final ObservableList<ObservableValue<String>> downloadLog;
    private final ReadOnlyIntegerWrapper downloadTotal;
    private final ReadOnlyIntegerWrapper completedTotal;
    private final ReadOnlyIntegerWrapper failedTotal;
    private final IntegerBinding remainingTotal;

    private final ReadOnlyStringWrapper status;
    private final ReadOnlyBooleanWrapper startDisabled;
    private final ReadOnlyBooleanWrapper shutDownDisabled;
    private final ReadOnlyBooleanWrapper hardShutDownDisabled;

    private ReadOnlyBooleanWrapper running;

    private final String COMPLETE = "All downloads complete";
    private final String USER_TERMINATED = "Program terminated by user.";

    public BatchDownloadVM(Collection<T> tasks)  {

        running = new ReadOnlyBooleanWrapper(false);
        service =  initializeService();
        pendingTasks = tasks.iterator();
        Callback<ObservableValue<String>, Observable[]> extractor = property -> new Observable[]{property};
        downloadLog = FXCollections.observableArrayList(extractor);

        runningViewModels = FXCollections.observableArrayList();
        taskToVMReference = new HashMap<>();

        downloadTotal = new ReadOnlyIntegerWrapper(0);
        completedTotal = new ReadOnlyIntegerWrapper(0);
        failedTotal = new ReadOnlyIntegerWrapper(0);

        remainingTotal = Bindings.createIntegerBinding( () -> {

            return tasks.size() - completedTotal.get() - failedTotal.get();
        }, completedTotal, failedTotal);

        startDisabled = new ReadOnlyBooleanWrapper(false);
        shutDownDisabled = new ReadOnlyBooleanWrapper(true);
        hardShutDownDisabled = new ReadOnlyBooleanWrapper(true);
        status = new ReadOnlyStringWrapper("Press start to download");

    }

    private ExecutorService initializeService() {

        RejectedExecutionHandler handler = (runnable, executor) -> {
            try {
                if (!service.isShutdown()) {
                    executor.getQueue().put(runnable);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        var blocker = new SynchronousQueue<Runnable>();
        var service = new ThreadPoolExecutor(5, 5, 10, TimeUnit.SECONDS, blocker);
        service.setRejectedExecutionHandler(handler);
        return service;
    }

    public void setDownloadTotal(int downloadTotal) {
        if (!running.get()) {
            this.downloadTotal.set(downloadTotal);
            completedTotal.set(downloadTotal - remainingTotal.get());
        } else System.out.println("Cannot change total. Download already started.");
    }

    public void handleStart()  {
        running.set(true);
        startDisabled.set(true);
        shutDownDisabled.set(false);
        hardShutDownDisabled.set(false);
        startService();
        status.set("Downloading");
    }

    private void startService() {

        new Thread( () -> {
            while (pendingTasks.hasNext() && !service.isShutdown()) {
                T next = pendingTasks.next();
                Platform.runLater( () -> configureTask(next));
                service.submit(next);
            }

            if (!service.isShutdown()) {
                Platform.runLater(() -> end(COMPLETE));
            }
        }).start();
    }

    public BooleanProperty runningProperty() {
        return running;
    }

    private void configureTask(T task) {

            task.setOnRunning(e -> createViewModel(task));
            task.setOnFailed(e -> cleanUpTask(task, false));
            task.setOnSucceeded(e -> cleanUpTask(task, true));
            task.setOnCancelled(e -> cleanUpTask(task, false));
    }

    private void createViewModel(T task) {

        var viewModel = new DownloadTileVM(task);
        taskToVMReference.put(task, viewModel);
        runningViewModels.add(viewModel);
    }

    private void cleanUpTask(T task, boolean success) {

        downloadLog.add(0, task.messageProperty());
        updateTotals(success);
        runningViewModels.remove(taskToVMReference.remove(task));
    }

    private void updateTotals(boolean success) {
        if (success) {
            completedTotal.set(completedTotal.get() + 1);
        }
        else failedTotal.set(failedTotal.get() + 1);
    }

    public void handleShutDown()  {

        shutDownDisabled.set(true);
        String shutDown = "Program terminating after active downloads complete.";
        status.set(shutDown);
        service.shutdown();

        new Thread(() -> {
            try {
                service.awaitTermination(2, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Platform.runLater(() -> end(USER_TERMINATED));
            }
        }).start();
    }

    public void handleHardShutDown() {

        end(USER_TERMINATED);
    }

    private void end(String status) {

        shutDownDisabled.set(true);
        hardShutDownDisabled.set(true);
        service.shutdownNow();
        this.status.set(status);
        running.set(false);
    }

    public ObservableList<DownloadTileVM> runningViewModelsProperty() {
        return runningViewModels;
    }

    public ReadOnlyIntegerProperty downloadTotalProperty() {
        return downloadTotal.getReadOnlyProperty();
    }

    public ReadOnlyIntegerProperty failedTotalProperty() {
        return failedTotal.getReadOnlyProperty();
    }

    public ReadOnlyIntegerProperty completedTotalProperty() {
        return completedTotal.getReadOnlyProperty();
    }

    public IntegerBinding remainingTotalProperty() {
        return remainingTotal;
    }

    public ObservableList<ObservableValue<String>> downloadLogProperty() {
        return downloadLog;
    }

    public ReadOnlyBooleanProperty startDisabledProperty() {
        return startDisabled.getReadOnlyProperty();
    }

    public ObservableValue<? extends Boolean> shutDownDisabledProperty() {
        return shutDownDisabled.getReadOnlyProperty();
    }

    public ObservableValue<? extends Boolean> hardShutDownDisabledProperty() {
        return hardShutDownDisabled.getReadOnlyProperty();
    }

    public ObservableValue<? extends String> statusProperty() {
        return status.getReadOnlyProperty();
    }
}

