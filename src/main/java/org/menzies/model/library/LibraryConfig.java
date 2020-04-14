package org.menzies.model.library;

import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.util.HashMap;

public interface LibraryConfig {

    File getCSV();
    String getDefaultSubDir(CSVRecord record);
    String getSource(CSVRecord record);
    HashMap<String, String> getDefaultTags(CSVRecord record);
    String [] getHeaders();


    }
