package org.menzies.model.service.tagging;

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

    }
}
