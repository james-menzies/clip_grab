package org.menzies.model;

import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class CSVDownloadTaskFactoryTest {

    CSVDownloadTaskFactory factory;


    @Before
    public void before() throws IOException {

        factory = new CSVDownloadTaskFactory("C:/Test Sounds/", getClass().getResource("/sample.csv")) {

            @Override
            protected String setFileDir(CSVRecord record) {
                return String.format("%s/%s/%s.wav", record.get(1), record.get(3), record.get(0));
            }

            @Override
            protected String setDownloadLocation(CSVRecord record) {
                return String.format("www.mywebsite.com/%s", record.get(2));
            }
        };

    }


    @Test
    public void print() {


        List<DownloadTask> list = factory.get();

        for (DownloadTask task : list) {
            System.out.println(task);
        }
    }

}