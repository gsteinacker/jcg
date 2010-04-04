package initialization;

import java.util.Locale;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public class InitializationExample {
    long ts1;
    {   // this is not yet supported
        ts1 = System.currentTimeMillis();
    }
    long ts2 = System.currentTimeMillis(); // neither is this...
    final String bar = "foo";              // or any other kind of initialization...
    static final String FOO = "bar";       // static by, I'm working on it.
    static final String FOOBAR = FOO +"test";
    static final Locale GERMAN = new Locale("de");
    static final Locale ENGLISH;
    static { // no, even this is not yet possible.
        ENGLISH = Locale.ENGLISH;
    }
    int i;

    InitializationExample() {
        i = 2;
    }
}
