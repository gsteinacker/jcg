/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package example.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Bar {
    String fooBar();
}