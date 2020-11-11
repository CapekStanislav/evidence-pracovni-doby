package cz.stanislavcapek.evidencepd.evidence;

import cz.stanislavcapek.evidencepd.model.Mesic;
import cz.stanislavcapek.evidencepd.smeny.Smena;
import cz.stanislavcapek.evidencepd.model.FondPracovniDoby;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * An instance of class {@code Evidence}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZakladniEvidence implements Evidence {

    private cz.stanislavcapek.evidencepd.zamestnanec.Zamestnanec Zamestnanec;
    private Mesic mesic;
    private int rok;
    private FondPracovniDoby.DruhTydenniPracDoby tydenniPracDoba;
    private double predchoziMesic;
    private Map<Integer, Smena> smeny;


}
