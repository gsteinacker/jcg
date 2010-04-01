/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package example01;

import de.steinacker.jcg.annotation.TransformWith;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
@TransformWith({"AddGetters", "AddSetters"})
public class Artikel {
    long serialVersionUID;
    Preis preis;
}
