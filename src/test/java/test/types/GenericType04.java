/**
 * Copyright (c) 2010 by Guido Steinacker
 */
package test.types;

import java.util.List;

public final class GenericType04<T extends Number & Comparable<? super T>> {

    public String foo(final List<? super T> list) {
        return list.toString();
    }

    public <T extends Comparable<? super T>> void bar(final List<T> list) {
        System.out.println(list);
    }


}