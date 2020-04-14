package org.menzies.model;

import org.menzies.model.library.Library;
import org.menzies.model.service.parsing.FailedParseException;
import org.menzies.model.service.parsing.ParsingService;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class Project  {

/*POJO class for handling the project instances in persistence.*/
    public static final String DEFAULT = "$DEFAULT";
    private Library library;
    private String rootDir;
    private String subDir;
    private boolean defaultTags;
    private Map<String, String> customTags;
    private List<LibraryElement> elements;
    private CountDownLatch latch;

    public Project() {
    }


    public Project(Library library, String rootDir) throws FailedParseException {

        this(library, rootDir, DEFAULT, true, new HashMap<>());
    }

    public Project(Library library, String rootDir, String subDir, boolean defaultTags, Map<String, String> customTags) throws FailedParseException {
        this.library = library;
        this.rootDir = rootDir;
        this.subDir = subDir;
        this.defaultTags = defaultTags;
        this.customTags = customTags;
        initializeElements();
        latch = new CountDownLatch(0);

    }

    public void lock() {


        if (latch.getCount() == 0) {
            latch = new CountDownLatch(1);
        }
    }

    public void unlock() {
        latch.countDown();
    }


    private void initializeElements() throws FailedParseException {

        elements = ParsingService.parse(library, rootDir, subDir, defaultTags, customTags);
    }

    public Library getLibrary() {
        return library;
    }

    public String getRootDir() {
        return rootDir;
    }

    public String getSubDir() {
        return subDir;
    }

    public boolean isDefaultTags() {
        return defaultTags;
    }

    public Map<String, String> getCustomTags() {

        return Collections.unmodifiableMap(customTags);
    }

    public List<LibraryElement> getElements() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Collections.unmodifiableList(elements);
    }

}
