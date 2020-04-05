package org.menzies.model;

import javafx.concurrent.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystemException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadTask extends Task<File> {

    String fileDir;
    URL source;
    static Pattern folderDirPattern;


    static {
        folderDirPattern = Pattern.compile(".+\\\\");

    }

    public DownloadTask(String fileDir, URL source) {
        this.fileDir = fileDir;
        this.source = source;
    }



    @Override
    protected File call() throws Exception {

        File file = new File(fileDir);

        Matcher matcher  = folderDirPattern.matcher(fileDir);

        matcher.find();


        File folder = new File(matcher.group());


        if (!folder.mkdirs() || !file.createNewFile()) {
            throw new FileSystemException("Could not make file directory. " +
                    "Invalid name likely. File directory: " + fileDir );
        }







        HttpURLConnection connection;
        connection = (HttpURLConnection) source.openConnection();
        long size = connection.getContentLength();



        return null;
    }



}
