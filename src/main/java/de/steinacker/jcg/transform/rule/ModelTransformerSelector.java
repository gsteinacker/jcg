package de.steinacker.jcg.transform.rule;

import de.steinacker.jcg.transform.model.ModelMessage;
import de.steinacker.jcg.transform.type.TypeMessage;

import java.util.List;

/**
 * A Rule used to select a ModelTransformer for a ModelMessage.
 *
 * Copyright (c) 2010 by Guido Steinacker
 */
public interface ModelTransformerSelector extends Rule<ModelMessage, List<String>>{
}