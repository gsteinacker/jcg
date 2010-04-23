/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.transform.type;

import de.steinacker.jcg.transform.Transformer;

import java.util.List;

/**
 * A Transformer used to transform a single Type into a Type.
 *
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public interface TypeTransformer extends Transformer<TypeMessage, List<TypeMessage>> {
}
