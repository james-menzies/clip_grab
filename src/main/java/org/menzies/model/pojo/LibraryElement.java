package org.menzies.model.pojo;

import org.menzies.model.service.download.Downloadable;
import org.menzies.model.service.tagging.Taggable;

import javax.persistence.*;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
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

    @Column(name = "file_offset")
    private int fileOffset;

    @Column(name = "file_ext")
    private String fileExt;

    @Lob
    @Column(name = "tags")
    private Tag[] tags;

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

        String suffix = fileOffset > 0 ? String.format(" (%d)", fileOffset) : "";

        return new File(file + suffix + fileExt);
    }

    @Override
    public Set<Tag> getTags() {
        return Set.of(tags);
    }

    protected LibraryElement() {

    }

    public int getFileOffset() {
        return fileOffset;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileOffset(int fileOffset) {
        this.fileOffset = fileOffset;
    }

    @Override
    public String toString() {
        return String.format("Lib Element - Source: %s%n FileLoc: %s%n %s%n%n", source.toExternalForm(),
                file, Arrays.toString(tags));
    }

    private LibraryElement(boolean completed, URL source, String file, Tag[] tags, String fileExt) {
        this.completed = completed;
        this.source = source;
        this.file = file;
        this.tags = tags;
        fileOffset = 0;
        this.fileExt = fileExt;
    }

    public static class Builder {
        private boolean completed;
        private URL source;
        private String file;
        private Tag[] tags;
        private String fileExt;

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

        public Builder setFileExt(String fileExt) {
            this.fileExt = fileExt;
            return this;
        }

        public Builder setTags(Collection<Tag> tags) {

            this.tags = tags.toArray(new Tag[0]);
            return this;
        }

        public LibraryElement build()
            throws IllegalStateException {

            if (file == null || source == null || tags == null || fileExt == null ) {
                throw new IllegalStateException("Source and/or file " +
                        "not initialized in Library Element Builder.");
            }
            return new LibraryElement(completed, source, file, tags, fileExt);
        }
    }
}
