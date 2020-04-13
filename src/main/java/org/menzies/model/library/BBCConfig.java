package org.menzies.model.library;

import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import static org.menzies.utils.StringUtils.makeFileSafe;

public class BBCConfig implements LibraryConfig {

    public static final File sourceCSV;

    static {
        URL url = BBCConfig.class.getResource("/csv/BBC.csv");
        sourceCSV = new File(url.toExternalForm());
    }


    @Override
    public File getCSV() {
        return sourceCSV;
    }

    @Override
    public String getDefaultSubDir(CSVRecord record) {

        String cdName = makeFileSafe(record.get("CDName"));
        String cdNumber = makeFileSafe(record.get("CDNumber"));
        String fileName = makeFileSafe(record.get("description"));
        String category = makeFileSafe(record.get("category"));


        StringBuilder builder = new StringBuilder();

        if (category.length() > 0 && category.length() < 64) {
            builder.append(category);
        }

        builder.append('/');

        if (cdName.length() > 0) {
            builder.append(cdName);
        } else builder.append("Unknown Album");

        if ( cdName.length() == 0 && cdNumber.length() > 0 ) {
            builder.append(String.format(" (CD: %s)", cdNumber));
        }

        builder.append(String.format("/%s.wav", fileName));

        return builder.toString();
    }

    @Override
    public String getSource(CSVRecord record) {
        return "http://bbcsfx.acropolis.org.uk/assets/" + record.get(0) ;
    }

    @Override
    public HashMap<String, String> getDefaultTags(CSVRecord record) {
        HashMap<String, String> tags = new HashMap<>();

        tags.put("ARTIST", "BBC");
        tags.put("GENRE", "Sound Effects");
        tags.put("ALBUM", "BBC: " + record.get("CDName"));
        tags.put("TITLE", record.get("description"));

        return tags;
    }
}
