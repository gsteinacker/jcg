/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package test.generics;

import java.util.Collections;
import java.util.List;

public final class GenericType05<T> extends GenericType01<T> {

    public List<T> asList(final T t) {
        return Collections.singletonList(t);
    }

}