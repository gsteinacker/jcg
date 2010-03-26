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
public @interface ImmutableBean {
    public enum Style {
        BUILDER, CONSTRUCTOR
    }

    public Style style() default Style.CONSTRUCTOR;

    public String template() default "ImmutableBean";

}