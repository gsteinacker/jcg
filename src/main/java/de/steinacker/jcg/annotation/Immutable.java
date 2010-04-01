/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Immutable {

    /** The style of the Immutable defines, how instances of the
     * generated classes are initialized. Using a constructor, a
     * build or using factory methods.
     */
    public enum Style {
        BUILDER, CONSTRUCTOR /*, TODO: FACTORY_METHOD*/
    }

    public Style style() default Style.CONSTRUCTOR;

    //public String factoryMethod() default "";

    //public String[] parameters() default {};

    public String transformWith() default "Immutable";

}