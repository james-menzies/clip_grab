package org.menzies.model;

import de.saxsys.javafx.test.JfxRunner;
import de.saxsys.javafx.test.TestInJfxThread;
import javafx.scene.Parent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.menzies.model.service.download.DownloadTask;
import org.menzies.model.service.download.Downloadable;
import org.menzies.utils.JFXUtil;
import org.menzies.viewmodel.DownloadTileVM;

import java.io.File;
import java.net.MalformedURLException;
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

        Downloadable downloadable = new Downloadable() {
            @Override
            public File getFile() {
                return new File("C://test/sample.wav");
            }

            @Override
            public URL getSource()  {
                try {
                    return new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public boolean isCompleted() {
                return false;
            }

            @Override
            public void setCompleted(boolean completed) {

            }
        };

        DownloadTask task = new DownloadTask(downloadable);
        ExecutorService service = Executors.newSingleThreadExecutor();
        DownloadTileVM vm = new DownloadTileVM(task);
        service.submit(task);
        Parent root = JFXUtil.getRoot(vm, "/org/menzies/view/DownloadTile.fxml");
        JFXUtil.createStage(root).showAndWait();
    }

}