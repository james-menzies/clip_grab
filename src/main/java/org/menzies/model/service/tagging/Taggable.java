package org.menzies.model.service.tagging;

import org.menzies.model.pojo.Tag;

import java.io.File;
import java.util.Set;

public interface Taggable {

    File getFile();
    Set<Tag> getTags();
}
