package de.steinacker.jcg.transform.rule;

import de.steinacker.jcg.transform.type.TypeMessage;

import java.util.List;

/**
 * A Rule used to select a TypeTransformer for a TypeMessage.
 * 
 * Copyright (c) 2010 by Guido Steinacker
 */
public interface TypeTransformerSelector extends Rule<TypeMessage, List<String>>{
}
