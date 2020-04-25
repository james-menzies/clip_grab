package org.menzies.model.service.tagging;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

public class JAudioTagger implements AudioTagger {

    private AudioFile file;
    private Tag tag;


    /*
    * Returns false if file is already set. Must be called before
    * addTag,
    */
    @Override
    public boolean setFile(File file) {

        if (this.file != null) {
            return false;
        }

        try {
            this.file = AudioFileIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        tag = this.file.getTag();
        return true;
    }

    @Override
    public boolean addTag(String ID, String value) {


        try {
            tag.setField(FieldKey.valueOf(ID), value);
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Tag ID does not exist.");
            return false;
        } catch (NullPointerException e) {
            System.out.println("Either file has not been set, or tag strings are null.");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean clearTags() {

        try {
            file.delete();
        } catch (CannotReadException e) {
            e.printStackTrace();
            return false;
        } catch (CannotWriteException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean commit() {

        try {
            AudioFileIO.write(file);
            return true;
        } catch (CannotWriteException e) {
            e.printStackTrace();
            return false;
        }
    }
}
