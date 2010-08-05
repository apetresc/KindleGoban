package com.amazon.kindle.app.go.model.sgf;

import java.io.File;
import java.io.FilenameFilter;

public class SGFFilenameFilter implements FilenameFilter {

    public boolean accept(File dir, String name) {
        return name.endsWith(".sgf");
    }

}
