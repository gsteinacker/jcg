/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package test.types;

import java.util.Collections;
import java.util.List;

public class GenericType01<T> {

    public String get(final T t) {
        return t.toString();
    }

    public List<T> asList(final T t) {
        return Collections.singletonList(t);
    }

}
