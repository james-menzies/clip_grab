package org.menzies.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.concurrent.Worker;


public class DownloadTileVM {

    private ReadOnlyStringWrapper downloadInfo;
    private ReadOnlyDoubleWrapper progress;

    public DownloadTileVM(Worker<?> worker) {

        progress = new ReadOnlyDoubleWrapper();
        progress.bind(worker.progressProperty());




        downloadInfo = new ReadOnlyStringWrapper();
        downloadInfo.bind(Bindings.when(Bindings.equal(progress, 1.0, 0.0001))
                .then(worker.messageProperty())
                .otherwise(Bindings.format("%s (%.2fMB of %.2fMB)",
                        worker.messageProperty(), worker.workDoneProperty(),
                        worker.totalWorkProperty())));




    }

    public ReadOnlyDoubleProperty progressProperty() {
        return progress.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty downloadInfoProperty() {
        return downloadInfo.getReadOnlyProperty();
    }

}
