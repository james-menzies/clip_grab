package org.menzies.viewmodel;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ListBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.util.Callback;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class BatchDownloadVM <T extends Task<?>> {


    private final ExecutorService service;
    private final Iterator<T> pendingTasks;
    private final ObservableList<T> submittedTasks;
    private final ObservableList<DownloadTileVM> activeList;

    private final ObservableList<String> downloadLog;
    private final ReadOnlyIntegerWrapper downloadTotal;
    private final ReadOnlyIntegerWrapper completedTotal;
    private final ReadOnlyIntegerWrapper failedTotal;
    private final IntegerBinding remainingTotal;

    private final ReadOnlyStringWrapper status;
    private final ReadOnlyBooleanWrapper startDisabled;
    private final ReadOnlyBooleanWrapper shutDownDisabled;
    private final ReadOnlyBooleanWrapper hardShutDownDisabled;

    private final String SHUTTING_DOWN = "Program terminating after active downloads complete.";
    private final String COMPLETE = "All downloads complete";
    private final String USER_TERMINATED = "Program terminated by user.";

    public BatchDownloadVM(ExecutorService service, List<T> tasks)  {

        this.service = service;
        pendingTasks = tasks.iterator();
        downloadLog = FXCollections.observableArrayList();

        submittedTasks = initializeSubmittedTasks();
        activeList = initializeActiveList();

        downloadTotal = new ReadOnlyIntegerWrapper();
        downloadTotal.set(tasks.size());

        completedTotal = new ReadOnlyIntegerWrapper();
        completedTotal.set(0);

        failedTotal = new ReadOnlyIntegerWrapper();
        failedTotal.set(0);

        remainingTotal = (IntegerBinding) Bindings.subtract(downloadTotal,
                Bindings.add(completedTotal, failedTotal));


        startDisabled = new ReadOnlyBooleanWrapper(false);
        shutDownDisabled = new ReadOnlyBooleanWrapper(true);
        hardShutDownDisabled = new ReadOnlyBooleanWrapper(true);
        status = new ReadOnlyStringWrapper("Press start to download");


        addCompletionListener(service);
        submittedTasks.addListener(this::ensureSubmittedTasksSize);
    }

    private void ensureSubmittedTasksSize(ListChangeListener.Change<? extends T> change)  {

        while (change.next()) {
            if (change.getList().size() <= 10) {
                activatePendingTasks(10);
                System.out.println("10 tasks added for submission");
            }
        }
    }

    private void addCompletionListener(ExecutorService service) {

        remainingTotal.addListener((observable, oldValue, newValue) -> {
                    if (newValue.intValue() == 0) {
                        performCompletionOperation();
                    }
                });
    }

    private void performCompletionOperation() {
        status.set(COMPLETE);
        shutDownDisabled.set(true);
        hardShutDownDisabled.set(true);
        service.shutdown();
    }

    private ListBinding<DownloadTileVM> initializeActiveList() {
        return new ListBinding<DownloadTileVM>() {
            {
                super.bind(submittedTasks);
            }

            @Override
            protected ObservableList<DownloadTileVM> computeValue() {
                List<DownloadTileVM> target = submittedTasks.stream()
                        .filter( worker -> worker.getState() == Worker.State.RUNNING)
                        .map(DownloadTileVM::new)
                        .collect(Collectors.toUnmodifiableList());

                return FXCollections.observableList(target);
            }
        };
    }

    private ObservableList<T> initializeSubmittedTasks() {

        Callback<T, Observable[]> extractor =
                worker -> new Observable[] {worker.stateProperty()};

        return FXCollections.observableArrayList(extractor);
    }

    private void activatePendingTasks(int amount) {

        for (int i=0; i < amount; i++) {
            T task = pendingTasks.next();
            service.submit(task);
            submittedTasks.add(task);
            task.setOnFailed(e -> onTaskCompletion(task));
            task.setOnSucceeded(e -> onTaskCompletion(task));
            task.setOnCancelled(e -> onTaskCompletion(task));
        }
    }

    private void onTaskCompletion(T task) {

        downloadLog.add(task.getMessage());
        submittedTasks.remove(task);
    }

    public void handleStart() throws InterruptedException {

        startDisabled.set(true);
        shutDownDisabled.set(false);
        hardShutDownDisabled.set(false);
        status.set("Downloading");
        activatePendingTasks(20);
    }

    public void handleShutDown() throws InterruptedException {

        shutDownDisabled.set(true);
        status.set(SHUTTING_DOWN);
        service.shutdown();

        Bindings.size(activeList).addListener( (v, o, n) -> {
            if (n.intValue() == 0) {
                hardShutDownDisabled.set(true);
                status.set(USER_TERMINATED);
            }
        });
    }

    public void handleHardShutDown() {

        shutDownDisabled.set(true);
        hardShutDownDisabled.set(true);
        service.shutdownNow();
        status.set(USER_TERMINATED);
    }


    public ObservableList<DownloadTileVM> activeListProperty() {
        return activeList;
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

    public ObservableList<String> downloadLogProperty() {
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

