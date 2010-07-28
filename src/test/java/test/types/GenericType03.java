/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package test.types;

import java.util.List;

public final class GenericType03<T> {

    public <S extends Number> S get(final Long t) {
        return null;
    }


    public void foo(final List<? extends Number> list) {
        System.out.println(list.toString());
    }

}