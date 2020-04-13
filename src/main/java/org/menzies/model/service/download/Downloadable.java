package org.menzies.model.service.download;

import java.io.File;
import java.net.URL;

public interface Downloadable {

    File getFile();
    URL getSource();
    boolean isCompleted();
    void setCompleted(boolean completed);
}
