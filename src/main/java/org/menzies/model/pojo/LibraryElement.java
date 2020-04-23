package org.menzies.model.pojo;

import org.menzies.model.service.download.Downloadable;
import org.menzies.model.service.tagging.Taggable;

import javax.persistence.*;
import java.io.File;
import java.net.URL;
import java.util.Set;


@Entity
@Table(name = "library_element")
public class LibraryElement implements Taggable, Downloadable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "completed")
    private boolean completed;

    @Column(name = "source")
    private URL source;

    @Column(name = "file", length = 511)
    private String file;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "library_element_id")
    private Set<Tag> tags;

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public URL getSource() {
        return source;
    }

    @Override
    public File getFile() {
        return new File(file);
    }

    @Override
    public Set<Tag> getTags() {
        return tags;
    }

    protected LibraryElement() {

    }

    @Override
    public String toString() {
        return String.format("Lib Element - Source: %s%n FileLoc: %s%n %s%n%n", source.toExternalForm(),
                file, tags.toString());
    }

    private LibraryElement(boolean completed, URL source, String file, Set<Tag> tags) {
        this.completed = completed;
        this.source = source;
        this.file = file;
        this.tags = tags;
    }

    public static class Builder {
        private boolean completed;
        private URL source;
        private String file;
        private Set<Tag> tags;

        public Builder() {

            completed = false;
        }

        public Builder setCompleted(boolean completed) {
            this.completed = completed;
            return this;
        }

        public Builder setSource(URL source) {
            this.source = source;
            return this;
        }

        public Builder setFile(String file) {
            this.file = file;
            return this;
        }

        public Builder setTags(Set<Tag> tags) {

            this.tags = tags;
            return this;
        }


        public LibraryElement build()
            throws IllegalStateException {

            if (file == null || source == null || tags == null) {
                throw new IllegalStateException("Source and/or file " +
                        "not initialized in Library Element Builder.");
            }
            return new LibraryElement(completed, source, file, tags);
        }
    }
}
