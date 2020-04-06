package org.menzies.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.menzies.viewmodel.DownloadTileVM;

import java.util.Objects;

public class DownloadTileView implements View<DownloadTileVM> {

    private DownloadTileVM viewModel;

    @FXML
    ProgressBar progressBar;

    @FXML
    Label downloadText;



    @Override
    public void setVM(DownloadTileVM vm) {
        if (Objects.isNull(viewModel)) {
            viewModel = vm;
            configureView();
        }

        else System.err.println("org.menzies.view.View Model for Download Tile already set.");
    }

    private void configureView() {

        progressBar.progressProperty().bind(viewModel.progressProperty());
        downloadText.textProperty().bind(viewModel.downloadInfoProperty());
        viewModel.failedProperty().addListener( (observable, oldValue, newValue) -> {
            if (newValue) {
                progressBar.setStyle("-fx-accent: red;");
            }
        });
    }


}
