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


    public void tagFile(Taggable taggable) {





    }


}
