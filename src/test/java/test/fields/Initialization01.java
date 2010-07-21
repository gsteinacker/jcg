package test.fields;

import java.util.Locale;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public class Initialization01 {
    long ts1;
    {
        ts1 = System.currentTimeMillis();
    }
    long ts2 = System.currentTimeMillis();
    final String bar = "foo";
    static final String FOO = "bar";
    static final String FOOBAR = FOO +"test";
    static final Locale GERMAN = new Locale("de");
    static final Locale ENGLISH;
    static {
        ENGLISH = Locale.ENGLISH;
    }
    int i;

    public Initialization01() {
        i = 2;
    }

    public Initialization01(int j) {
        this.i = j;
    }

}