package org.menzies.model;

import de.saxsys.javafx.test.JfxRunner;
import de.saxsys.javafx.test.TestInJfxThread;
import javafx.scene.Parent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.menzies.utils.JFXUtil;
import org.menzies.viewmodel.DownloadTileVM;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@RunWith(JfxRunner.class)
public class DownloadTaskTest

{

    @TestInJfxThread
    @Test
    public void sampleDownload() throws Exception {


        String url =  "http://bbcsfx.acropolis.org.uk/assets/07076051.wav";


        DownloadTask task = new DownloadTask("C://test/sample.wav" , url);


        ExecutorService service = Executors.newSingleThreadExecutor();


        DownloadTileVM vm = new DownloadTileVM(task);

        service.submit(task);

        Parent root = JFXUtil.getRoot(vm, "/DownloadTile.fxml");



        JFXUtil.createStage(root).showAndWait();


    }

}