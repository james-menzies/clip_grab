package org.menzies.view;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import org.menzies.model.SetUpFlow;
import org.menzies.viewmodel.BatchDownloadVM;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class Director {

    private SetUpFlow flow;
    private BorderPane dynamicScene;
    private String rootDir = "C:/BBC";
    private List<? extends Task<?>> workerList;
    private ThreadPoolExecutor service;

    public Director(BorderPane dynamicScene, SetUpFlow flow) {

        this.flow = flow;
        this.dynamicScene = dynamicScene;
    }

    public void start() throws IOException {

 /*       workerList = flow
                .startNewFlow(rootDir)
                .generateWorkers();
*/
        service = flow.getService();


        var viewModel = new BatchDownloadVM(service, workerList);
        changeScene(viewModel, "/BatchDownload.fxml");
        viewModel.handleStart();


    }


    private <T> void changeScene(T viewModel, String resource) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(resource));
        dynamicScene.setCenter(loader.load());
        View<T> view = loader.getController();
        view.setVM(viewModel);
    }
}
