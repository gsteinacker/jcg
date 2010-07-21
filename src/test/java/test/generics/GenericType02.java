/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package test.generics;

public final class GenericType02<T extends Number> {

    <S> GenericType02(S s) {}

    public long get(final T t) {
        return t.longValue();
    }

}