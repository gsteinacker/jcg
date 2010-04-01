/*
 * Copyright (c) 2010 by Guido Steinacker
 */
package example02;

import de.steinacker.jcg.annotation.TransformWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */

@TransformWith({"AddGetters", "AddSetters", "AddConstructors"})
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface MyTransformer {
}
