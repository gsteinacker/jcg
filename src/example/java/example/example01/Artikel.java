/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package example.example01;

import de.steinacker.jcg.annotation.TransformWith;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
@TransformWith(value = {"TypeMapper", "TypeTranslator", "AddGetters", "AddSetters"})
public class Artikel {
    Preis preis;
}
