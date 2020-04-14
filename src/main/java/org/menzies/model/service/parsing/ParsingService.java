package org.menzies.model.service.parsing;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.menzies.model.LibraryElement;
import org.menzies.model.Project;
import org.menzies.model.library.Library;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

public class ParsingService {

    private Library library;
    private String subDirRegex;
    private String rootDir;
    boolean defaultTags;
    Map<String, String> customTagRegexs;
    Iterator<CSVRecord> iterator;

    private ParsingService(Library library, String rootDir, String subDirRegex, boolean defaultTags, Map<String, String> customTagRegexs) {
        this.library = library;
        this.subDirRegex = subDirRegex;
        this.rootDir = rootDir;
        this.defaultTags = defaultTags;
        this.customTagRegexs = customTagRegexs;
    }

    public static List<LibraryElement> parse(Library library, String subDirRegex, String rootDir,
                                             boolean defaultTags, Map<String, String> customTags) throws FailedParseException {

        return new ParsingService(library, subDirRegex, rootDir, defaultTags, customTags).run();
    }

    private List<LibraryElement> run() throws FailedParseException {



        List<LibraryElement> list = new ArrayList<>();
        iterator = initializeParser().iterator();

        while (iterator.hasNext()) {
           list.add(processRecord(iterator.next()));
        }

        return list;
    }

    private String getLiteralFromRegex(CSVRecord record, String regex, String phase) throws FailedParseException {

        throw new FailedParseException(phase, "Bad Regex inputted from user");
    }

    private CSVParser initializeParser() throws FailedParseException {
        try {
            return CSVParser.parse(library.getConfig().getCSV(),
                    Charset.defaultCharset(), CSVFormat.DEFAULT.withHeader(library.getConfig().getHeaders()));
        } catch (IOException e) {
            throw new FailedParseException("Parser Startup","I/O error Parser initialization.");
        } catch (IllegalArgumentException e) {
            throw new FailedParseException("Parser Startup", "Either library passed to parser or reference to its csv is null.");
        }
    }

    private LibraryElement processRecord(CSVRecord record) throws FailedParseException {
        URL source;
        File file;
        Map<String, String> literalTags;


        try {
            source = new URL(library.getConfig().getSource(record));
        } catch (MalformedURLException e) {
            throw new FailedParseException("Download Parse", "Library provided an invalid URL (internal error)");
        }

        String subDir;

        if (subDirRegex == Project.DEFAULT) {

            subDir = library.getConfig().getDefaultSubDir(record);
        }
        else subDir = getLiteralFromRegex(record, subDirRegex, "File Name Parse");

        file = new File(rootDir + subDir);

        literalTags = new HashMap<>();

        for (String category :
                customTagRegexs.keySet()) {

            String literalTagValue = getLiteralFromRegex(record,
                    customTagRegexs.get(category), "Tag Parse");
        }

        if (defaultTags) {
            literalTags.putAll(library.getConfig().getDefaultTags(record));
        }

        return new LibraryElement.Builder()
                .setCompleted(false)
                .setFile(file)
                .setSource(source)
                .setTags(literalTags)
                .build();
    }

}
