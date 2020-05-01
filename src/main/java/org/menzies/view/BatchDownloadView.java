package org.menzies.view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.menzies.viewmodel.BatchDownloadVM;

import java.net.URL;

public class BatchDownloadView implements View<BatchDownloadVM<?>> {


    private BatchDownloadVM<?>  viewModel;
    
    
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
    private ListView<ObservableValue<String>> downloadLog;
    @FXML
    private VBox dropInForDownloads;
    @FXML
    private Label status;

    private NodeListDisplay<StringProperty> display;

    @Override
    public void setVM(BatchDownloadVM<?> vm) {
        
        if (viewModel == null) {
            this.viewModel = vm;
            configureView();
        }
        else System.err.println("ViewModel for Batch Download already set!");
    }

    private void configureView() {

        URL url = getClass().getResource("/org/menzies/view/DownloadTile.fxml");
        var display = new NodeListDisplay<>
                (url, dropInForDownloads, viewModel.runningViewModelsProperty());

        downloadTotal.setText(String.valueOf(viewModel.downloadTotalProperty().get()));
        failedTotal.textProperty().bind(Bindings.convert(viewModel.failedTotalProperty()));
        completedTotal.textProperty().bind((Bindings.convert(viewModel.completedTotalProperty())));
        remainingTotal.textProperty().bind(Bindings.convert(viewModel.remainingTotalProperty()));
        downloadLog.setItems(viewModel.downloadLogProperty());
        startButton.disableProperty().bind(viewModel.startDisabledProperty());
        shutdownButton.disableProperty().bind(viewModel.shutDownDisabledProperty());
        hardShutdownButton.disableProperty().bind(viewModel.hardShutDownDisabledProperty());
        status.textProperty().bind(viewModel.statusProperty());


        downloadLog.setCellFactory( listView -> new ListCell<ObservableValue<String>>() {

            @Override
            protected void updateItem(ObservableValue<String> item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setText("");
                }
                else {
                    setText(item.getValue());

                    if (item.getValue().toLowerCase().contains("failed") ||
                    item.getValue().toLowerCase().contains("cancelled")) {
                        setTextFill(Color.RED);
                    }
                    else setTextFill(Color.BLACK);
                }
            }


        });

        startButton.setOnAction(event -> viewModel.handleStart());
        shutdownButton.setOnAction(event -> viewModel.handleShutDown());
        hardShutdownButton.setOnAction(event -> viewModel.handleHardShutDown());

        downloadLog.setFocusTraversable(false);
    }


}
