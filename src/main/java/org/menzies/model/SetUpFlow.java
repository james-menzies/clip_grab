package org.menzies.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SetUpFlow {

    private String[] savedDownloads;



    public String[] getSavedDownloads() {
        return new String[0];
    }

    public ThreadPoolExecutor getService() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
    }

    public NewFlow startNewFlow(String destinationRoot) {

        return new NewFlow(destinationRoot);
    }


    public class NewFlow {

        private String destinationRoot;

        private NewFlow(String destinationRoot) {
            this.destinationRoot = destinationRoot;
        }


        public  List<DownloadTask> generateWorkers() {


            return null;
        }

    }



}
