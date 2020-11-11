package cz.stanislavcapek.evidencepd.evidence;

import cz.stanislavcapek.evidencepd.model.Mesic;
import cz.stanislavcapek.evidencepd.zamestnanec.Zamestnanec;
import cz.stanislavcapek.evidencepd.smeny.Smena;
import cz.stanislavcapek.evidencepd.model.FondPracovniDoby;

import java.util.Map;

/**
 * An instance of interface {@code Evidence}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public interface Evidence {

    Zamestnanec getZamestnanec();

    Mesic getMesic();

    int getRok();

    FondPracovniDoby.DruhTydenniPracDoby getTydenniPracDoba();

    double getPredchoziMesic();

    Map<Integer, Smena> getSmeny();
}
