/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package test.types;

import java.util.List;

public final class GenericType06<S, T extends Number & Comparable<? super T>, U> {

    public String foo(final List<? super S> list) {
        return list.toString();
    }

    public <S extends Comparable<? super T>> void bar(final List<U> list) {
        System.out.println(list);
    }


}