package org.menzies.model;

import org.menzies.model.service.download.Downloadable;
import org.menzies.model.service.tagging.Taggable;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LibraryElement implements Taggable, Downloadable {


    private boolean completed;
    private URL source;
    private File file;
    private Map<String, String> tags;

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
        return file;
    }

    @Override
    public Map<String, String> getTags() {
        return tags;
    }

    protected LibraryElement() {

    }

    @Override
    public String toString() {
        return String.format("Lib Element. Source: %s FileLoc: %s Tags: %s", source.toExternalForm(),
                file.getAbsolutePath(), tags.toString());
    }

    public LibraryElement(boolean completed, URL source, File file, Map<String, String> tags) {
        this.completed = completed;
        this.source = source;
        this.file = file;
        this.tags = tags;
    }

    public static class Builder {
        private boolean completed;
        private URL source;
        private File file;
        private Map<String, String> tags;

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

        public Builder setFile(File file) {
            this.file = file;
            return this;
        }

        public Builder setTags(Map<String, String> tags) {

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
