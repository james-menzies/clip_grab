package org.menzies.model.service.parsing;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.menzies.model.pojo.LibraryElement;
import org.menzies.model.pojo.Project;
import org.menzies.model.pojo.Tag;
import org.menzies.model.pojo.TagTemplate;
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
    private boolean defaultTags;
    private Set<TagTemplate> tagTemplates;
    private Iterator<CSVRecord> iterator;

    private ParsingService(Library library, String rootDir, String subDirRegex, boolean defaultTags, Set<TagTemplate> tagTemplates) {
        this.library = library;
        this.subDirRegex = subDirRegex;
        this.rootDir = rootDir;
        this.defaultTags = defaultTags;
        this.tagTemplates = tagTemplates;
    }

    public static Set<LibraryElement> parse(Library library, String subDirRegex, String rootDir,
                                             boolean defaultTags, Set<TagTemplate> tagTemplates) throws FailedParseException {

        return new ParsingService(library, subDirRegex, rootDir, defaultTags, tagTemplates).generate();
    }

    private Set<LibraryElement> generate() throws FailedParseException {

        Set<LibraryElement> libraryElements = new HashSet<>();
        iterator = initializeParser().iterator();

        while (iterator.hasNext()) {
           libraryElements.add(processRecord(iterator.next()));
        }

        return libraryElements;
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
        String file;
        Set<Tag> tags;

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

        file = rootDir + subDir;

        tags = new HashSet<>();

        for (TagTemplate template :
                tagTemplates) {

            String value = getLiteralFromRegex(record, template.getRegex(),
                    String.format("Custom Tag Parse(%s)", template.getField()));
            String field = template.getField();

            Tag tag = new Tag(field, value);
            tags.add(tag);
        }

        if (defaultTags) {

            tags.addAll(library.getConfig().getDefaultTags(record));
        }

        return new LibraryElement.Builder()
                .setCompleted(false)
                .setFile(file)
                .setSource(source)
                .setTags(tags)
                .build();
    }

}
