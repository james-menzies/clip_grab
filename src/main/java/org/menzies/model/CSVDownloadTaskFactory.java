package org.menzies.model;

import javafx.util.Callback;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.menzies.model.tagging.AudioTagger;
import org.menzies.model.tagging.JAudioTagger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class CSVDownloadTaskFactory
        implements DownloadTaskFactory {


    private final String rootDir;
    private final CSVParser parser;
    private final HashMap<String, Callback<CSVRecord, String>> tags;
    private Supplier<AudioTagger> taggerFactory;
    private BiFunction<String, String, DownloadTask> downloadTaskFactory;

    public CSVDownloadTaskFactory(String rootDir, URL sourceCSV) throws IOException {
        this.rootDir = rootDir;
        parser = CSVParser.parse(sourceCSV, Charset.defaultCharset(), CSVFormat.DEFAULT.withHeader());
        tags = new HashMap<>();
        taggerFactory = JAudioTagger::new;
        downloadTaskFactory = DownloadTask::new;
    }

    public void setAudioTaggerFactory(Supplier<AudioTagger> taggerFactory) {

        this.taggerFactory = taggerFactory;
    }

    public BiFunction<String, String, DownloadTask> getDownloadTaskFactory() {
        return downloadTaskFactory;
    }

    @Override
    public List<DownloadTask> get() {

        List<DownloadTask> list = new ArrayList<>();

        for (CSVRecord record : parser) {

            DownloadTask task = createDownloadTask(record);
            //TODO: Figure out how to remove duplicates.
            list.add(task);
        }
        return list;
    }

    private DownloadTask createDownloadTask(CSVRecord record) {

        String fileDir = rootDir + setFileDir(record);
        String downloadLocation = setDownloadLocation(record);
        var task = downloadTaskFactory.apply(fileDir, downloadLocation);
        task.addPostDownloadOperation(file -> addTags(file, record));
        return task;
    }


    private void addTags(File file, CSVRecord currentRecord) {

        AudioTagger tagger = taggerFactory.get();
        tagger.setFile(file);

        for (String category : tags.keySet()) {
            tagger.addTag(category, tags.get(category).call(currentRecord));
        }
        tagger.commit();
    }

    /*produces the directory not including the root directory.*/
    protected abstract String setFileDir(CSVRecord record);

    protected abstract String setDownloadLocation(CSVRecord record);

    protected void addTag(String category, Callback<CSVRecord, String> function) {

        tags.put(category, function);
    }


}
