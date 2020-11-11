/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.stanislavcapek.evidencepd.zamestnanec;

import javax.swing.AbstractListModel;
import java.util.*;

/**
 * Utility třída pro práci se seznamem zaměstnanců. Jedná se o návrhový vzor Singlton, takže jde vytvořit pouze jednu
 * instanci, která je pak na požádání poskytnuta přes metodu getInstance().
 *
 * @author Stanislav Čapek
 */
public class ZamestnanciListModel extends AbstractListModel {
    private static ZamestnanciListModel instance;
    private final List<Zamestnanec> seznam;

    /**
     * Privátní konstruktor bez parametru. Návrhový model Singlton.
     */
    private ZamestnanciListModel() {
        this.seznam = new ArrayList<>();
    }

    /**
     * Metoda pro získání instance ZamestnanciListModel.
     *
     * @return ZamestnanciListModel
     */
    public static ZamestnanciListModel getInstance() {
        if (instance == null) {
            instance = new ZamestnanciListModel();
        }
        return instance;
    }

    /**
     * Metoda pro přidání zaměstnance (strážníka) do seznamu. V případě pokusu o přidání strážníka se stejným služebním číslem (ID)
     * metoda vrací {@code false}.
     *
     * @param str Zamestnanec
     * @return boolean <br>
     * {@code true} - přidání proběhlo <br>
     * {@code false} - přidání neproběhlo (již existuje strážník se stejným ID)
     * @see Zamestnanec
     */
    public boolean pridejZamestnance(Zamestnanec str) {
        int index = seznam.size();
        boolean vysledek = false;
        if (vyhledejPodleID(str.getId()) == null) {
            vysledek = seznam.add(str);
            seradPodleID();
            fireIntervalAdded(this, index, index);
        }
        return vysledek;
    }

    /**
     * Metoda pro odebrání zaměstnance (strážníka) ze seznamu.
     *
     * @param str Zamestnanec
     * @return {@code true} - došlo k odebrání
     */
    public boolean odeberZamestnance(Zamestnanec str) {
        int index = seznam.indexOf(str);
        boolean vysledek = seznam.remove(str);
        seradPodleID();
        fireIntervalRemoved(this, index, index);
        return vysledek;
    }

    /**
     * Metoda vymaže SEZNAM zaměstnanců. Pozor, jedná se o jedinou instanci, dojde tedy k vymazání všech odkazů
     * na jednotlivé zaměstnance.
     *
     * @return {@code true} - došlo k vymazaní
     */
    public boolean vymazSeznam() {
        int index1 = seznam.size() - 1;
        boolean vysledek = seznam.removeAll(seznam);
        if (index1 >= 0) {
            fireIntervalRemoved(this, 0, index1);
        }
        return vysledek;
    }

    /**
     * Vyhledání zaměstnance (strážníka) v seznamu podle jeho ID (služební číslo).
     *
     * @param id String
     * @return {@link Zamestnanec} - jestliže je nalezen<br>
     * {@code null} - jestliže není nalezen žádný strážník
     */
    public Zamestnanec vyhledejPodleID(int id) {
        Zamestnanec zamestnanec = null;

        for (Zamestnanec value : seznam) {
            int strId = value.getId();
            if (strId == id) {
                zamestnanec = value;
            }
        }
        return zamestnanec;
    }

    /**
     * Upozorní {@link javax.swing.event.ListDataListener} na změnu v seznamu.
     */
    public void obnovModel() {
        super.fireContentsChanged(this, 0, 0);
    }

    /**
     * Returns the length of the list.
     *
     * @return the length of the list
     */
    @Override
    public int getSize() {
        return seznam.size();
    }

    /**
     * Returns the value at the specified index.
     *
     * @param index the requested index
     * @return the value at <code>index</code>
     */
    @Override
    public Zamestnanec getElementAt(int index) {
        return seznam.get(index);
    }

    /**
     * Vrátí kopii aktuálního stavu seznamu zaměstnanců jako pole {@link Zamestnanec}.
     *
     * @return kopie aktuálního stavu seznamu
     */
    public Zamestnanec[] toArray() {
        return seznam.toArray(new Zamestnanec[getSize()]);
    }

    /**
     * Vrátí kopii aktuální stavu seznamu zaměstnanců jako seznam {@link Zamestnanec}
     * @return
     */
    public List<Zamestnanec> getSeznam() {
        return new ArrayList<>(seznam);
    }

    /**
     * Metoda, která seřadí zaměstnance podle jejich služebního čísla (ID) od nejmenšího po největší.
     */
    private void seradPodleID() {
//        int size = seznam.size();
//        Zamestnanec str;
//        boolean zmena = true;
//        while (zmena) {
//            zmena = false;
//            for (int i = 0; i < size; i++) {
//                for (int j = 0; j < size - 1; j++) {
//                    if (seznam.get(j).getId() >
//                            seznam.get(j + 1).getId()) {
//                        str = seznam.get(j);
//                        seznam.remove(j);
//                        seznam.add(j + 1, str);
//                        zmena = true;
//                    }
//                }
//            }
//        }
        Collections.sort(seznam, Comparator.comparing(Zamestnanec::getId));
    }
}
