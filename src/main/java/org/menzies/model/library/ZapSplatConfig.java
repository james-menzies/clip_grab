package org.menzies.model.library;

import org.apache.commons.csv.CSVRecord;
import org.menzies.model.pojo.Tag;

import java.io.File;
import java.util.Set;



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
    public Set<Tag> getDefaultTags(CSVRecord record) {
        return null;
    }

    @Override
    public String[] getHeaders() {
        return new String[0];
    }

    @Override
    public String getDescription() {
        return "Library not yet available for download.";
    }

    @Override
    public String getFileExt() {
        return ".mp3";
    }
}
