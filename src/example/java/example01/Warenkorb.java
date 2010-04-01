/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package example01;

import de.steinacker.jcg.annotation.Immutable;
import de.steinacker.jcg.annotation.Mutable;

import java.util.List;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
@Mutable
public final class Warenkorb {
    private Kunde kunde;
    private List artikel;
}
