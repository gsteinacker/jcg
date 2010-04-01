/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark source types as mutable beans.
 * @author Guido Steinacker
 * @version %version: 28 %
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Mutable {

    public String transformWith() default "Mutable";

}