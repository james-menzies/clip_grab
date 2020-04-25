package org.menzies.model.service.download;

import javafx.concurrent.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;


public class DownloadTask extends Task<File> {

    private File file;
    private final URL downloadLocation;
    private final CountDownLatch latch;
    private boolean failTask;
    private final List<Consumer<File>> postDownloadTasks;
    private URLConnection conn;
    private ReadableByteChannel inputChannel;
    private FileChannel outputChannel;


    public DownloadTask(Downloadable downloadable) {

        file = downloadable.getFile();
        downloadLocation = downloadable.getSource();
        postDownloadTasks = new ArrayList<>();
        latch = new CountDownLatch(2);
    }


    @Override
    protected File call() throws Exception {
        //TODO Refactor method into smaller pieces
        updateMessage("Initializing: " + file.getName());

        Thread fileThread = new Thread(this::initializeFile);
        fileThread.start();
        Thread connectionThread = new Thread(this::initializeURLConnection);
        connectionThread.start();

        latch.await();

        if (failTask) {
            throw new Exception();
        }

        long totalSize = conn.getContentLength();
        long currentIndex = 0;
        long packetSize = 1024;

        updateMessage(String.format("Downloading: %s", file.getName()));
        while (currentIndex < totalSize) {

            outputChannel.transferFrom(inputChannel, currentIndex, packetSize);
            updateProgress(outputChannel.size(), totalSize);

            currentIndex += packetSize;
        }

        for (Consumer<File> operation : postDownloadTasks) {

            operation.accept(file);
        }

        inputChannel.close();
        outputChannel.close();

        updateMessage("Performing post download tasks...");

        for (Consumer<File> task : postDownloadTasks) {
            task.accept(file);
        }

        updateMessage(String.format("Downloaded %s", file.getName()));
        return file;
    }

    private void initializeURLConnection() {


        try {
            conn = downloadLocation.openConnection();
        } catch (IOException | IllegalArgumentException e) {
            updateMessage(String.format("Download of %s failed. Invalid URL.", file.getName()));
            failTask = true;
        }

        try {
            inputChannel = Channels.newChannel(conn.getInputStream());
        } catch (IOException e) {
            updateMessage(String.format("Download of %s failed. Could not find website.", file.getName()));
            failTask = true;
        } finally {
            latch.countDown();
        }
    }

    private void initializeFile() {
        //TODO This still feels prone to errors in read-only contexts
        if (file.exists()) {
            file.delete();
        }

        try {
            file.getParentFile().mkdirs();
            if (!file.exists()) {
                file.createNewFile();
            }
            outputChannel = new FileOutputStream(file).getChannel();

        } catch (IOException e) {
            updateMessage(String.format("Download of %s failed. Could not create file.", file.getName()));
            e.printStackTrace();
            failTask = true;
        } finally {
            latch.countDown();
        }
    }

    public void addPostDownloadTask(Consumer<File> task) {

        postDownloadTasks.add(task);
    }

    @Override
    protected void failed() {


        if (!failTask) {

            updateMessage("Download Cancelled");
        }
        super.failed();
        file.delete();

        // TODO: 24/04/2020 Make sure this won't delete everything before inclusion!
//
//        File parent = file.getParentFile();
//        while (parent.isDirectory() && parent.list().length == 0) {
//            parent.delete();
//            parent = parent.getParentFile();
//        }
    }

    //Standard overrides and getters


    public URL getDownloadLocation() {
        return downloadLocation;
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return file.getName();
    }

    @Override
    public String toString() {

        String tagsAsString;

        return String.format("Download task [from: %s to: %s]",
                downloadLocation, file.getAbsolutePath());
    }
}
