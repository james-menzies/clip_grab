package org.menzies.model.service.parsing;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.menzies.model.LibraryElement;
import org.menzies.model.library.Library;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParsingService {

    private Library library;
    private String fileDir;
    boolean defaultTags;
    Map<String, String> customTags;
    CSVParser parser;

    public ParsingService(Library library, String fileDir, boolean defaultTags, Map<String, String> customTags) {
        this.library = library;
        this.fileDir = fileDir;
        this.defaultTags = defaultTags;
        this.customTags = customTags;

    }

    public static List<LibraryElement> parse(Library library, String fileDir,
                                             boolean defaultTags, Map<String, String> customTags) throws IOException {

        return new ParsingService(library, fileDir, defaultTags, customTags).run();
    }

    private List<LibraryElement> run() throws IOException {

        List<LibraryElement> list = new ArrayList<>();
        parser = initializeParser();




        return list;


    }

    private CSVParser initializeParser() throws IOException {
        try {
            return CSVParser.parse(library.get().getCSV(),
                    Charset.defaultCharset(), CSVFormat.DEFAULT);
        } catch (IOException e) {
            throw new IOException("I/O error during CSV Parser initialization.");
        } catch (IllegalArgumentException e) {
            throw new IOException("Either library passed to parser or reference to its csv is null.");
        }
    }
}
