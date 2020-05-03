package org.menzies.model.service.parsing;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.menzies.model.library.Library;
import org.menzies.model.pojo.LibraryElement;
import org.menzies.model.pojo.Project;
import org.menzies.model.pojo.Tag;
import org.menzies.model.pojo.TagTemplate;

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
    private int fileRenameCounter;

    private ParsingService(Library library, String rootDir, String subDirRegex, boolean defaultTags, Set<TagTemplate> tagTemplates) {
        this.library = library;
        this.subDirRegex = subDirRegex;
        this.rootDir = rootDir;
        this.defaultTags = defaultTags;
        this.tagTemplates = tagTemplates;
        fileRenameCounter = 0;
    }

    public static Set<LibraryElement> parse(Library library, String subDirRegex, String rootDir,
                                            boolean defaultTags, Set<TagTemplate> tagTemplates) throws FailedParseException {

        return new ParsingService(library, subDirRegex, rootDir, defaultTags, tagTemplates).generate();
    }

    private Set<LibraryElement> generate() throws FailedParseException {


        Set<LibraryElement> libraryElements = new TreeSet<>(getComparator());
        Iterator<CSVRecord> iterator = initializeParser().iterator();

        while (iterator.hasNext()) {

            LibraryElement nextElement = processRecord(iterator.next());
            int offsetCounter = 0;

            while (!libraryElements.add(nextElement)) {

                offsetCounter++;
                nextElement.setFileOffset(offsetCounter);
            }

            if (offsetCounter > 0) {
                fileRenameCounter++;
            }
        }

        System.out.println("Number of files renamed: " + fileRenameCounter);

        return libraryElements;
    }

    private Comparator<LibraryElement> getComparator() {
        return (l1, l2) -> {

            String s1 = l1.getFile().getAbsolutePath();
            String s2 = l2.getFile().getAbsolutePath();

            if (!s1.equals(s2)) {
                return s1.compareTo(s2);
            } else {
                return l1.getFileOffset() - l2.getFileOffset();
            }
        };

    }

    private String getLiteralFromRegex(CSVRecord record, String regex, String phase) throws FailedParseException {

        throw new FailedParseException(phase, "Bad Regex inputted from user");
    }

    private CSVParser initializeParser() throws FailedParseException {
        try {
            return CSVParser.parse(library.getConfig().getCSV(),
                    Charset.defaultCharset(), CSVFormat.DEFAULT.withHeader(library.getConfig().getHeaders()));
        } catch (IOException e) {
            throw new FailedParseException("Parser Startup", "I/O error Parser initialization.");
        } catch (IllegalArgumentException e) {
            throw new FailedParseException("Parser Startup", "Either the library passed to parser or its source information does not exist.");
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
        } else subDir = getLiteralFromRegex(record, subDirRegex, "File Name Parse");

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
                .setFileExt(library.getConfig().getFileExt())
                .build();
    }

}
