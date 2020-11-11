package cz.stanislavcapek.evidencepd.smeny.sluzebnici;

import cz.stanislavcapek.evidencepd.smeny.Priplatky;
import cz.stanislavcapek.evidencepd.smeny.Smena;

/**
 * Instance rozhraní {@code PocitadloPriplatku}
 *
 * @author Stanislav Čapek
 */
public interface PocitadloPriplatku {

    Priplatky vypoctiPriplatky(Smena smena);
}
