package org.menzies.view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.menzies.viewmodel.BatchDownloadVM;
import org.menzies.viewmodel.DownloadTileVM;

import java.net.URL;

public class BatchDownloadView implements View<BatchDownloadVM> {


    private BatchDownloadVM viewModel;
    
    
    @FXML
    private Label failedTotal;
    @FXML
    private Label completedTotal;
    @FXML
    private Label downloadTotal;
    @FXML
    private Label remainingTotal;
    @FXML
    private Button startButton;
    @FXML
    private Button shutdownButton;
    @FXML
    private Button hardShutdownButton;
    @FXML
    private ListView<String> downloadLog;
    @FXML
    private VBox dropInForDownloads;
    @FXML
    private Label status;

    private NodeListDisplay<StringProperty> display;

    @Override
    public void setVM(BatchDownloadVM vm) {
        
        if (viewModel == null) {
            this.viewModel = vm;
            configureView();
        }
        else System.err.println("ViewModel for Batch Download already set!");
    }

    private void configureView() {

        URL url = getClass().getResource("/DownloadTile.fxml");
        var display = new NodeListDisplay<DownloadTileVM>
                (url, dropInForDownloads, viewModel.activeListProperty());

        downloadTotal.setText(String.valueOf(viewModel.downloadTotalProperty().get()));
        failedTotal.textProperty().bind(Bindings.convert(viewModel.failedTotalProperty()));
        completedTotal.textProperty().bind((Bindings.convert(viewModel.completedTotalProperty())));
        remainingTotal.textProperty().bind(Bindings.convert(viewModel.remainingTotalProperty()));
        downloadLog.setItems(viewModel.downloadLogProperty());
        startButton.disableProperty().bind(viewModel.startDisabledProperty());
        shutdownButton.disableProperty().bind(viewModel.shutDownDisabledProperty());
        hardShutdownButton.disableProperty().bind(viewModel.hardShutDownDisabledProperty());
        status.textProperty().bind(viewModel.statusProperty());


        startButton.setOnAction(event -> viewModel.handleStart());
        shutdownButton.setOnAction(event -> {
            try {
                viewModel.handleShutDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        hardShutdownButton.setOnAction(event -> viewModel.handleHardShutDown());

        downloadLog.setFocusTraversable(false);
    }


}
