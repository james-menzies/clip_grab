package org.menzies.view;

import de.saxsys.javafx.test.JfxRunner;
import de.saxsys.javafx.test.TestInJfxThread;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.menzies.utils.ADWFactory;
import org.menzies.utils.AutomaticDummyWorker;
import org.menzies.utils.JFXUtil;
import org.menzies.viewmodel.BatchDownloadVM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

@RunWith(JfxRunner.class)
public class BatchDownloadViewTest  {

    List<AutomaticDummyWorker> workers;
    ThreadPoolExecutor service;

    @Before
    public void before() {

        workers = new ArrayList<>();
        workers.addAll(ADWFactory.get(10));

        service = new ThreadPoolExecutor(5, 5, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>()) {
            @Override
            public void shutdown() {
                super.shutdown();
                getQueue().clear();
            }
        };






    }



//    @Ignore
    @Test
    @TestInJfxThread
    public void demonstration() throws IOException {

        var viewModel = new BatchDownloadVM<AutomaticDummyWorker>
                (service, workers);

        Parent root = JFXUtil.getRoot(viewModel, "/BatchDownload.fxml");


        JFXUtil.createStage(root).showAndWait();
    }

    @Test
    @TestInJfxThread
    public void blank() {

        new Stage().showAndWait();
    }


}
