package org.menzies.model.service.tagging;

import org.junit.Before;
import org.junit.Test;
import org.menzies.model.pojo.Tag;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TaggingServiceTest {

    Taggable taggable;
    Set<Tag> tags;

    @Before
    public void before() {

        tags = new HashSet<>();
        tags.add(new Tag("TITLE", "NOT A RIVER"));
        tags.add(new Tag("ARTIST", "Some Legend"));
        tags.add(new Tag("GENRE", "Sound Effects"));
        tags.add(new Tag("TRACK", "42"));
        tags.add(new Tag("ALBUM", "BBC Sound Effects"));

        taggable = new Taggable() {
            @Override
            public File getFile() {
                return new File("/D:/River.wav");
            }

            @Override
            public Set<Tag> getTags() {
                return tags;
            }
        };
    }

    @Test
    public void addSingleTag() {

        System.out.println(taggable.getFile().exists());

        TaggingService service = new TaggingService();


        service.tagFile(taggable);
    }

}