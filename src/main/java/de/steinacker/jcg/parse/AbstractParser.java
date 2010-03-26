/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.parse;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
abstract class AbstractParser implements Parser {

    private final static Logger LOG = Logger.getLogger(AbstractParser.class);

    /**
     * This method accepts one or more file names.
     * A list of valid file objects will be created and
     * returned to main method.
     *
     * @return List of valid source file objects
     */
    protected final List<File> getFilesAsList(final String dir, final boolean recursive) {
        final File sourceDir = new File(dir);
        if (!sourceDir.isDirectory())
            LOG.warn(dir + " is not a directory.");
        if (!sourceDir.canRead())
            LOG.warn(dir + " is not readable.");

        // read all java files from directory:
        final String[] fileNames = sourceDir.list(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".java");
            }
        });
        // addParameter all readable files to the result list:
        final List<File> result = new ArrayList<File>(fileNames.length);
        for (final String fileName : fileNames) {
            final File sourceFile = new File(dir + '/' + fileName);
            if (sourceFile.exists() && sourceFile.canRead()) {
                result.add(sourceFile);
            } else {
                LOG.warn(fileName + " is not readable. Ignoring the file. ");
            }
        }
        // if recursive is true, recursively read all subdirectories:
        if (recursive) {
            final File[] subDirectories = sourceDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            for (final File subDirectory : subDirectories) {
                result.addAll(getFilesAsList(subDirectory.getPath(), true));
            }
        }
        return result;
    }
}
