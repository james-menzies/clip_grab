package org.menzies.model.library;

import org.apache.commons.csv.CSVRecord;
import org.menzies.model.pojo.Tag;

import java.io.File;
import java.util.Set;

public interface LibraryConfig {

    File getCSV();
    String getDefaultSubDir(CSVRecord record);
    String getSource(CSVRecord record);
    Set<Tag> getDefaultTags(CSVRecord record);
    String [] getHeaders();

    String getDescription();

    String getFileExt();

    }
