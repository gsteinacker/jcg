/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.parse;

import de.steinacker.jcg.model.Model;

/**
 * A Parser reads source code and returns a Model, representing the code.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public interface Parser {

    /**
     * Parses the specified source file, and returns a Model, representing the code.
     *
     * @param sourceFile the source file.
     * @param binDir the output directory of the parser.
     * @return Model the Model created by the Parser
     */
    public Model parse(String sourceFile, String binDir);

    /**
     * Parses all source files in the specified directory, and returns a Model, representing the code.
     * Depending on the recursive flag, the directory is traversed recursively.
     *
     * @param sourceDir the source directory where code is read from.
     * @param recursive specifies whether the directory is traversed recursively.
     * @param binDir the output directory of the parser.
     * @return Model the Model created by the Parser
     */
    public Model parse(String sourceDir, boolean recursive, String binDir);
}
