package org.menzies.model;

import de.saxsys.javafx.test.JfxRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;


@RunWith(JfxRunner.class)
public class DownloadTaskTest

{

    @Test
    public void sampleDownload() throws Exception {


        URL url = new URL("http://bbcsfx.acropolis.org.uk/assets/07076051.wav");
        DownloadTask task = new DownloadTask(System.getProperty("user.home") + "\\Desktop\\yay\\bingo.txt", url);


        task.call();

    }

}