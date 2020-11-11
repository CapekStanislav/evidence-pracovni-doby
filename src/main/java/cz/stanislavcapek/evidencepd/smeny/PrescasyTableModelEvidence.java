package cz.stanislavcapek.evidencepd.smeny;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.stanislavcapek.evidencepd.evidence.Evidence;
import cz.stanislavcapek.evidencepd.model.FondPracovniDoby;
import cz.stanislavcapek.evidencepd.model.Mesic;
import cz.stanislavcapek.evidencepd.smeny.sluzebnici.NastavovacCasuSmeny;
import cz.stanislavcapek.evidencepd.zamestnanec.Zamestnanec;
import cz.stanislavcapek.evidencepd.utils.Zaokrouhlovac;
import lombok.ToString;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Instance třídy {@code PrescasyTable}
 * <p>
 * vždy seřazeno dle datumu
 *
 * @author Stanislav Čapek
 */
@JsonIgnoreProperties({"rowCount", "columnCount", "tableModelListeners"})
@ToString
public class PrescasyTableModelEvidence extends AbstractTableModel implements Evidence {

    private final String[] columnNames = {"den", "od", "do", "typ", "odpr. hodin", "noční", "víkend", "svátek"};
    private final Evidence evidence;
    private final Zaokrouhlovac zaokrouhlovac = new Zaokrouhlovac() {
    };
    private final List<Smena> smenaList;

    public PrescasyTableModelEvidence(Evidence prescasy) {
        evidence = prescasy;
        smenaList = new ArrayList<>(evidence.getSmeny().values());
        usporadejAOznamZmenu();
    }

    @Override
    public int getRowCount() {
        return smenaList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return this.columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final Smena prescas = smenaList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return prescas.getZacatek().toLocalDate().format(DateTimeFormatter.ofPattern("d. M"));
            case 1:
                return prescas.getZacatek().toLocalTime();
            case 2:
                return prescas.getKonec().toLocalTime();
            case 3:
                return prescas.getTypSmeny();
            case 4:
                return zaokrouhli(prescas.getPracovniDoba().getOdpracovano());
            case 5:
                return zaokrouhli(prescas.getPriplatky().getNocni());
            case 6:
                return zaokrouhli(prescas.getPriplatky().getVikend());
            case 7:
                return zaokrouhli(prescas.getPriplatky().getSvatek());
            default:
                return "";
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        final Smena prescas = smenaList.get(rowIndex);
        // TODO: 19.10.2020 dodělat stejnou implementaci jako v případě SmenyTableModel

//        LocalTime localTime = null;
//        if (aValue instanceof String) {
//            try {
//                localTime = ziskejCas((String) aValue);
//            } catch (DateTimeParseException ignore) {
//                return;
//            }
//        }
//        switch (columnIndex) {
//            case 1:
//                if (localTime != null) {
//                    prescas.setZacatek(zacatek.with(localTime));
//                }
//                break;
//            case 2:
//                if (localTime != null) {
//                    prescas.setKonec(konec.with(localTime));
//                }
//                break;
//        }
        switch (columnIndex) {
            case 1:
            case 2:
                if (columnIndex == 1) {
                    NastavovacCasuSmeny.nastavCas(prescas, aValue, NastavovacCasuSmeny.TypCasu.ZACATEK);
                } else {
                    NastavovacCasuSmeny.nastavCas(prescas, aValue, NastavovacCasuSmeny.TypCasu.KONEC);
                }
                usporadejAOznamZmenu();
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("Pro sloupec %s není nastavena editace.", columnIndex)
                );
        }

    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1 || columnIndex == 2;
    }

    /**
     * Přidá do seznamu přesčas
     *
     * @param prescas nová přesčasová směna
     */
    public void addPrescas(Smena prescas) {
        smenaList.add(prescas);
        usporadejAOznamZmenu();
    }


    /**
     * Odebrání směny ze seznamu
     *
     * @param index pořadí směny
     */
    public void removePrescas(int index) {
        smenaList.remove(index);
        usporadejAOznamZmenu();
    }

    @Override
    public Zamestnanec getZamestnanec() {
        return evidence.getZamestnanec();
    }

    @Override
    public Mesic getMesic() {
        return evidence.getMesic();
    }

    @Override
    public int getRok() {
        return evidence.getRok();
    }

    @Override
    public FondPracovniDoby.DruhTydenniPracDoby getTydenniPracDoba() {
        return evidence.getTydenniPracDoba();
    }

    @Override
    public double getPredchoziMesic() {
        return evidence.getPredchoziMesic();
    }

    @Override
    public Map<Integer, Smena> getSmeny() {
        final Map<Integer, Smena> map = new TreeMap<>();
        for (int i = 0; i < smenaList.size(); i++) {
            final Smena smena = smenaList.get(i);
            map.put(i, smena);
        }
        return map;
    }

    private void usporadejAOznamZmenu() {
        sortList();
        this.fireTableDataChanged();
    }

    private double zaokrouhli(double toRound) {
        return zaokrouhlovac.getRoundedDouble(toRound, 2);
    }

    private void sortList() {
        smenaList.sort(Comparator.comparing(Smena::getZacatek));
    }

}
