package org.menzies.model;

import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.net.URL;

import static org.menzies.utils.StringUtils.makeFileSafe;

public class DefaultBBCDownloadTaskFactory extends CSVDownloadTaskFactory {

    public static final URL sourceCSV;

    static {
        sourceCSV = DefaultBBCDownloadTaskFactory.class.getResource("/csv/BBC.csv");
    }

    public DefaultBBCDownloadTaskFactory(String rootDir) throws IOException {
        super(rootDir, sourceCSV);

    }

    @Override
    protected String setFileDir(CSVRecord record) {

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
    protected String setDownloadLocation(CSVRecord record) {
        return "http://bbcsfx.acropolis.org.uk/assets/" + record.get(0) ;
    }
}
