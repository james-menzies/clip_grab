package org.menzies.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.concurrent.Worker;


public class DownloadTileVM {

    private ReadOnlyStringWrapper downloadInfo;
    private ReadOnlyDoubleWrapper workDone;
    private ReadOnlyDoubleWrapper total;
    private ReadOnlyBooleanWrapper failed;
    private final long bytesInMb = 1_048_576;
    private ReadOnlyDoubleWrapper progress;

    public ReadOnlyBooleanProperty failedProperty() {
        return failed.getReadOnlyProperty();
    }

    public DownloadTileVM(Worker<?> worker) {

        workDone = new ReadOnlyDoubleWrapper();
        total = new ReadOnlyDoubleWrapper();
        workDone.bind(Bindings.divide(worker.workDoneProperty(), bytesInMb));
        total.bind(Bindings.divide(worker.totalWorkProperty(), bytesInMb));


        progress = new ReadOnlyDoubleWrapper();
        progress.bind(worker.progressProperty());




        downloadInfo = new ReadOnlyStringWrapper();
        downloadInfo.bind(Bindings.when(Bindings.and(worker.runningProperty(),
                Bindings.lessThan(workDone, 0.0)))
                .then(worker.messageProperty())
                .otherwise(Bindings.format("%s (%.1fMB of %.1fMB)",
                        worker.messageProperty(), workDone, total)));


        failed = new ReadOnlyBooleanWrapper(false);

        failed.bind(Bindings.createBooleanBinding( () -> {

            return worker.getState() == Worker.State.FAILED;
        }, worker.stateProperty()));



    }

    public ReadOnlyDoubleProperty progressProperty() {
        return progress.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty downloadInfoProperty() {
        return downloadInfo.getReadOnlyProperty();
    }

}
