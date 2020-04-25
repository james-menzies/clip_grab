package org.menzies.model.pojo;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.menzies.model.library.Library;
import org.menzies.model.service.parsing.FailedParseException;
import org.menzies.model.service.parsing.ParsingService;

import javax.persistence.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Entity
@Table(name = "project")
public class Project  {

/*POJO class for handling the project instances in persistence.*/
    public static final String DEFAULT = "$DEFAULT";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "library")
    @Enumerated(EnumType.STRING)
    private Library library;

    @Column(name = "root_directory")
    private String rootDir;

    @Column(name = "sub_directory")
    private String subDir;

    @Column(name = "default_tags_used")
    private boolean defaultTagsUsed;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @JoinColumn(name = "project_id")
    private Set<TagTemplate> tagTemplates;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @JoinColumn(name = "project_id")
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 10)
    private Set<LibraryElement> elements;

    @Transient
    private CountDownLatch latch;

    public Project() {
        latch = new CountDownLatch(0);
    }


    public Project(Library library, String rootDir) throws FailedParseException {

        this(library, rootDir, DEFAULT, true, new HashSet<>());
    }

    public Project(Library library, String rootDir, String subDir, boolean defaultTags, Set<TagTemplate> tagTemplates) throws FailedParseException {
        this.library = library;
        this.rootDir = rootDir;
        this.subDir = subDir;
        this.defaultTagsUsed = defaultTags;
        this.tagTemplates = tagTemplates;
        initializeElements();
        latch = new CountDownLatch(0);
    }

    public boolean lock() {

        if (latch.getCount() == 0) {
            latch = new CountDownLatch(1);
            return true;
        }
        else return false;
    }

    public boolean unlock() {
        if (latch.getCount() > 0) {
            latch.countDown();
            return true;
        }
        else return false;
    }


    private void initializeElements() throws FailedParseException {

        elements = ParsingService.parse(library, rootDir, subDir, defaultTagsUsed, tagTemplates);
    }

    public int getId() {
        return id;
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

    public boolean isDefaultTagsUsed() {
        return defaultTagsUsed;
    }

    public Set<TagTemplate> getTagTemplates() {

        return tagTemplates;
    }

    public Set<LibraryElement> getElements() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Collections.unmodifiableSet(elements);
    }
}
