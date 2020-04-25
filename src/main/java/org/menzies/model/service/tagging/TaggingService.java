package org.menzies.model.service.tagging;

import org.menzies.model.pojo.Tag;

import java.io.File;
import java.util.Set;
import java.util.function.Supplier;

public class TaggingService {

    private AudioTagger tagger;

    public TaggingService() {

        tagger = new JAudioTagger();
    }

    public TaggingService(Supplier<AudioTagger> taggerFactory) {

        tagger = taggerFactory.get();
    }

    // TODO: 13/04/2020 implement tag method
    public void tagFile(Taggable taggable) {

        Set<Tag> tags = taggable.getTags();
        File file = taggable.getFile();

        tagger.setFile(file);

        for (Tag tag : tags) {
            tagger.addTag(tag.getField(), tag.getValue());
        }
        tagger.commit();
    }
}
