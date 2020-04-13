package org.menzies.model.service.tagging;

import java.io.File;
import java.util.Map;

public interface Taggable {

    File getFile();
    Map<String, String> getTags();
}
