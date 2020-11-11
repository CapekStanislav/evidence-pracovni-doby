package cz.stanislavcapek.evidencepd.smeny.sluzebnici;

import cz.stanislavcapek.evidencepd.smeny.PracovniDoba;
import cz.stanislavcapek.evidencepd.smeny.Smena;

/**
 * An instance of interface {@code PocitadloPracovniDoby}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public interface PocitadloPracovniDoby {

    PracovniDoba vypoctiPracovniDobu(Smena smena);
}
