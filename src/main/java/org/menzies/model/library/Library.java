package org.menzies.model.library;

import java.util.function.Supplier;

public enum Library implements Supplier<LibraryConfig> {

    BBC(BBCConfig::new),
    ZAPSPLAT(ZapSplatConfig::new),
    ;

    private Supplier<LibraryConfig> config;

    Library(Supplier<LibraryConfig> config) {

        this.config = config;
    }

    public LibraryConfig get() {

         return config.get();
    }
}
