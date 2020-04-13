package org.menzies.model.library;

import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.util.HashMap;

public class ZapSplatConfig implements LibraryConfig {

    @Override
    public File getCSV() {
        return null;
    }

    @Override
    public String getDefaultSubDir(CSVRecord record) {
        return null;
    }

    @Override
    public String getSource(CSVRecord record) {
        return null;
    }

    @Override
    public HashMap<String, String> getDefaultTags(CSVRecord record) {
        return null;
    }
}
