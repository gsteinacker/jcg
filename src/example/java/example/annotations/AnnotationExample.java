/**
 * Copyright (c) 2010 by Guido Steinacker
 */

package example.annotations;

import example.example01.Artikel;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/* This is only an example for different kinds of annotations. The
 * annotations do not make sense at all!
 */
@SuppressWarnings("foo")
public class AnnotationExample {

    @Valid
    Artikel item;

    @NotNull (message="foo", groups=String.class)
    Artikel articleWithStringAndTypeParams;

    @Size(min = 5, max=8)
    String stringWithSizeMinAndMaxParams;

    @NotNull (groups={String.class, Integer.class})
    String stringWithTwoTypeParams;

    @Foo(bar=@Bar(fooBar="42"))
    int intWithNestedAnnotations;

    @Valid
    void addAnotherArtikel(Artikel a) {
    }

    @Valid
    Artikel getSomeArtikel(String foo) {
        return item;
    }
}
