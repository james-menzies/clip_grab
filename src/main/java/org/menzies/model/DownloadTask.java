package org.menzies.model;

import javafx.concurrent.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadTask extends Task<File> {

    String fileDir;
    String source;
    CountDownLatch latch;
    String fileName;
    boolean failTask;

    File file;
    URLConnection conn;

    ReadableByteChannel inputChannel;
    FileChannel outputChannel;

    private static Pattern fileNameFinder =
            Pattern.compile("\\w+.+\\w");



    public DownloadTask(String fileDir, String source) {
        this.fileDir = fileDir;
        this.source = source;

        Matcher matcher = fileNameFinder.matcher(fileDir);

        matcher.find();
        fileName = matcher.group();
    }



    @Override
    protected File call() throws Exception {

        updateMessage("Initializing: " + fileDir);
        latch = new CountDownLatch(2);

        Thread fileThread = new Thread ( () -> initializeFile());
        fileThread.start();
        Thread connectionThread = new Thread ( () -> initializeURLConnection());
        connectionThread.start();

        latch.await();


        if(failTask) {
            throw new Exception();
        }

        ReadableByteChannel inputChannel = Channels.newChannel(conn.getInputStream());
        FileChannel outputChannel = new FileOutputStream(file).getChannel();

        long totalSize = conn.getContentLength();
        long currentIndex = 0;
        long packetSize = 2056;
        int counter = 0;


        updateMessage(String.format("Downloading: %s", fileName));
        while (currentIndex < totalSize) {

            outputChannel.transferFrom(inputChannel, currentIndex, packetSize);
            updateProgress(outputChannel.size(),totalSize);

            counter++;
            currentIndex += packetSize;
        }

        System.out.println("Counter: " + counter);

        updateMessage("Download successful");

        inputChannel.close();
        outputChannel.close();

        return  file;
    }

    private void initializeURLConnection()  {


        try {
            conn = new URL(source).openConnection();
        } catch (IOException|IllegalArgumentException e) {
            updateMessage(String.format("Download of %s failed. Invalid URL.", fileName ));
            failTask = true;
        }

        try {
            inputChannel = Channels.newChannel(conn.getInputStream());
        } catch (IOException e) {
            updateMessage(String.format("Download of %s failed. Could not find website.", fileName));
            failTask = true;
        } finally {
            latch.countDown();
        }
    }

    private void initializeFile()  {

        file = new File(fileDir);

        if (file.exists()) {
            file.delete();
        }

        try {
            if (!file.getParentFile().exists()) {
                file.mkdirs();
            }
            file.createNewFile();

        } catch (IOException e) {
            updateMessage(String.format("Download of %s failed. Could not create file.", fileName));
            failTask = true;
        } finally {
            latch.countDown();

        }
    }

    @Override
    protected void failed() {
        super.failed();
        file.delete();
    }
}
