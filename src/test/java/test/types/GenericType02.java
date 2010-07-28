/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package test.types;

public final class GenericType02<T extends Number> {

    <S> GenericType02(final S s) {}

    public long get(final T t) {
        return t.longValue();
    }

}