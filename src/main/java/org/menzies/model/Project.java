package org.menzies.model;

import org.menzies.model.library.Library;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Project  {

/*POJO class for handling the project instances in persistence.*/
    private String name;
    private Library library;
    private String rootDir;
    private String subDir;
    private boolean defaultTags;
    private Map<String, String> customTags;
    private List<LibraryElement> elements;

    public Project() {

    }


    public Project(Library library, String rootDir) {
        this.library = library;
        this.rootDir = rootDir;
    }


    public String getName() {
        return name;
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
        return customTags;
    }

    public List<LibraryElement> getElements() {
        return elements;
    }

}
