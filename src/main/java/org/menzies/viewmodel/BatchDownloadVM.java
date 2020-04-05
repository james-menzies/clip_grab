package org.menzies.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class BatchDownloadVM <T extends Task<?>> {


    private final ExecutorService service;
    private final Iterator<T> pendingTasks;
    private final ObservableList<DownloadTileVM> runningViewModels;
    private final Map<T, DownloadTileVM> taskToVMReference;

    private final IntegerProperty totalActivated;
    private final IntegerBinding currentlyActivated;

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
    private final int MINIMUM_ACTIVATED = 10;

    public BatchDownloadVM(ExecutorService service, List<T> tasks)  {

        this.service = service;
        pendingTasks = tasks.iterator();
        downloadLog = FXCollections.observableArrayList();

        runningViewModels = FXCollections.observableArrayList();

        taskToVMReference = new HashMap<>();
        downloadTotal = new ReadOnlyIntegerWrapper(tasks.size());
        completedTotal = new ReadOnlyIntegerWrapper(0);
        failedTotal = new ReadOnlyIntegerWrapper(0);
        remainingTotal = (IntegerBinding) Bindings.subtract(downloadTotal,
                Bindings.add(completedTotal, failedTotal));

        totalActivated = new SimpleIntegerProperty(0);
        currentlyActivated = initializeCurrentlyActivated();

        startDisabled = new ReadOnlyBooleanWrapper(false);
        shutDownDisabled = new ReadOnlyBooleanWrapper(true);
        hardShutDownDisabled = new ReadOnlyBooleanWrapper(true);
        status = new ReadOnlyStringWrapper("Press start to download");
    }

    private IntegerBinding initializeCurrentlyActivated() {

        return (IntegerBinding) Bindings.subtract(totalActivated,
                Bindings.add(completedTotal, failedTotal));
    }

    public void handleStart()  {
        startDisabled.set(true);
        shutDownDisabled.set(false);
        hardShutDownDisabled.set(false);
        status.set("Downloading");
        activatePendingTasks(20);
        currentlyActivated.addListener(this::onCurrentlyActivatedChange);
        addCompletionListener();
    }

    private void activatePendingTasks(int amount) {

        for (int i=0; i < amount; i++) {
            T task = pendingTasks.next();
            pendingTasks.remove();
            service.submit(task);
            task.setOnRunning(e -> createViewModel(task));
            task.setOnFailed(e -> cleanUpTask(task, false));
            task.setOnSucceeded(e -> cleanUpTask(task, true));
            task.setOnCancelled(e -> cleanUpTask(task, false));
        }

        totalActivated.set(totalActivated.get() + amount);
    }

    private void createViewModel(T task) {

        var viewModel = new DownloadTileVM(task);
        taskToVMReference.put(task, viewModel);
        runningViewModels.add(viewModel);
    }

    private void cleanUpTask(T task, boolean success) {

        downloadLog.add(0, task.getMessage());
        removeTaskListeners(task);
        updateTotals(success);
        runningViewModels.remove(taskToVMReference.remove(task));
    }

    private void removeTaskListeners(T task) {

        task.setOnRunning(null);
        task.setOnFailed(null);
        task.setOnSucceeded(null);
        task.setOnCancelled(null);

    }

    private void updateTotals(boolean success) {
        if (success) {
            completedTotal.set(completedTotal.get() + 1);
        }
        else failedTotal.set(failedTotal.get() + 1);
    }

    private void onCurrentlyActivatedChange(ObservableValue<? extends Number> value,
                                            Number oldValue, Number newValue) {

        if (newValue.intValue() < MINIMUM_ACTIVATED &&
                remainingTotal.get() > MINIMUM_ACTIVATED) {
            activatePendingTasks(10);
        }
    }

    private void addCompletionListener() {

        remainingTotal.addListener((observable, oldValue, newValue) -> {
                    if (newValue.intValue() == 0) {
                        end(COMPLETE);
                    }
                });
    }


    public void handleShutDown()  {

        shutDownDisabled.set(true);
        status.set(SHUTTING_DOWN);
        service.shutdown();

        Bindings.size(runningViewModels).addListener( (v, o, n) -> {
            if (n.intValue() == 0) {
                end(USER_TERMINATED);
            }
        });
    }

    public void handleHardShutDown() {

        end(USER_TERMINATED);
    }

    private void end(String status) {

        shutDownDisabled.set(true);
        hardShutDownDisabled.set(true);
        service.shutdownNow();
        this.status.set(status);
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

