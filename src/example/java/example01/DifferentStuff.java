package example01;

import java.util.Locale;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public class DifferentStuff {
    long ts1;
    {
        ts1 = System.currentTimeMillis();
    }
    long ts2 = System.currentTimeMillis();
    final String bar = "foo";
    static final String foo = "bar";
    static final String foobar = foo+"test";
    static final Locale german = new Locale("de");
    static final Locale english;
    static {
        english = Locale.ENGLISH;
    }
    int i;

    DifferentStuff() {
        i = 2;
    }
}
