/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.parse;

import de.steinacker.jcg.model.Model;
import de.steinacker.jcg.model.Type;
import org.apache.log4j.Logger;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * A JavaParser using the Java compiler API and annotation processors to parse Java
 * files.
 */
public final class JavaFileParser extends AbstractParser {

    private final static Logger LOG = Logger.getLogger(JavaFileParser.class);

    // Gets the Java programming language compiler
    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    public JavaFileParser() {
    }

    /**
     * Parses the specified Java source file, and returns a Model, representing the Java type.
     *
     * @param sourceFile the source file .
     * @return Model the Model created by the Parser
     */
    @Override
    public Model parse(final String sourceFile) {
        LOG.info("Parsing sourceFile " + sourceFile + "...");
        final Model model = parseFiles(Collections.singletonList(new File(sourceFile)));
        LOG.info("Done.");
        return model;
    }

    /**
     * Parses all Java files in the specified directory, and returns a Model, representing the Java types.
     * Depending on the recursive flag, the directory is traversed recursively.
     *
     * @param sourceDir the source directory where Java files are read from.
     * @param recursive specifies whether the directory is traversed recursively.
     * @return Model the Model created by the Parser
     */
    @Override
    public Model parse(final String sourceDir, final boolean recursive) {
        LOG.info((recursive ? "Recursively parsing directory " : "Parsing directory ") + sourceDir);
        final Model model = parseFiles(getFilesAsList(sourceDir, recursive));

        return model;
    }

    /**
     * Parses the specified Java files, and returns a Model, representing the Java types.
     *
     * @param files Java source code
     * @return Model
     */
    private Model parseFiles(final List<File> files) {
        LOG.info(files.size() + " files found.");
        final Model model;
        if (files.size() > 0) {
            // Get a new instance of the standard file manager implementation
            final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            // Get the list of java file objects
            Iterable<? extends JavaFileObject> compilationUnits1 = fileManager
                    .getJavaFileObjectsFromFiles(files);
            // Create the compilation task
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null,
                    null, null, compilationUnits1);
            // Set the annotation processor
            final JcgProcessor processor = new JcgProcessor();
            task.setProcessors(Collections.singletonList(processor));
            // Perform the compilation task.
            task.call();
            try {
                fileManager.close();
            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
            }
            model = processor.getModel();
        } else {
            LOG.warn("No valid source files to process.  Exiting from the program");
            model = new Model(Collections.<Type>emptyList());
        }
        LOG.info("Done.");
        return model;
    }

}