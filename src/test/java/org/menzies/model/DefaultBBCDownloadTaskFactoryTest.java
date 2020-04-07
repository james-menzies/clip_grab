package org.menzies.model;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class DefaultBBCDownloadTaskFactoryTest {


    @Test
    public void print() throws IOException {


        var factory = new DefaultBBCDownloadTaskFactory("C:/BBC/");

        List<DownloadTask> list = factory.get();

        for (DownloadTask task : list) {

            System.out.println(task);
        }




    }

}