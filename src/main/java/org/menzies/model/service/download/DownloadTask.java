package org.menzies.model.service.download;

import javafx.concurrent.Task;
import org.menzies.utils.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;


public class DownloadTask extends Task<File> {

    private File file;
    private String displayFile;
    private final URL downloadLocation;
    private final CountDownLatch latch;
    private boolean failTask;
    private final Map<Consumer<File>, String> postDownloadTasks;
    private URLConnection conn;
    private ReadableByteChannel inputChannel;
    private FileChannel outputChannel;


    public DownloadTask(Downloadable downloadable) {


        file = downloadable.getFile();
        displayFile = StringUtils.shorten(file.getName(), 50);
        downloadLocation = downloadable.getSource();
        postDownloadTasks = new LinkedHashMap<>();
        latch = new CountDownLatch(2);
    }


    @Override
    protected File call() throws Exception {
        //TODO Refactor method into smaller pieces
        updateMessage("Initializing: " + displayFile);

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

        updateMessage(String.format("Downloading: %s", displayFile));
        while (currentIndex < totalSize) {

            outputChannel.transferFrom(inputChannel, currentIndex, packetSize);
            updateProgress(outputChannel.size(), totalSize);

            currentIndex += packetSize;
        }


        for (Consumer<File> operation : postDownloadTasks.keySet()) {

            String message = postDownloadTasks.get(operation);

            try {
                updateMessage(message);
                operation.accept(file);
            } catch (Exception e) {
                updateMessage(String.format("Download of %s failed during phase: %s", displayFile, message));
                failTask = true;
                throw new Exception();
            }
        }

        inputChannel.close();
        outputChannel.close();

        updateMessage(String.format("Downloaded %s", displayFile));
        return file;
    }

    private void initializeURLConnection() {


        try {
            conn = downloadLocation.openConnection();
        } catch (IOException | IllegalArgumentException e) {
            updateMessage(String.format("Download of %s failed. Invalid URL.", displayFile));
            failTask = true;
        }

        try {
            inputChannel = Channels.newChannel(conn.getInputStream());
        } catch (IOException e) {
            updateMessage(String.format("Download of %s failed. Could not find website.", displayFile));
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
            updateMessage(String.format("Download of %s failed. Could not create file.", displayFile));
            e.printStackTrace();
            failTask = true;
        } finally {
            latch.countDown();
        }
    }

    public void addPostDownloadTask(Consumer<File> task, String desc) {

        postDownloadTasks.put(task, desc);
    }

    @Override
    protected void failed() {
        super.failed();
        if (!failTask) {
            updateMessage(String.format("Download of %s was cancelled.", displayFile));
        }


        if (file.delete()) {
            System.out.println(String.format("Partial file %s was deleted.", displayFile));
        }
        else System.out.println(String.format("Unable to delete partial file %s", displayFile));
    }


    //Standard overrides and getters


    public URL getDownloadLocation() {
        return downloadLocation;
    }

    public File getFile() {
        return file;
    }

    public String getFileName() {
        return displayFile;
    }

    @Override
    public String toString() {

        String tagsAsString;

        return String.format("Download task [from: %s to: %s]",
                downloadLocation, file.getAbsolutePath());
    }
}
