package cz.stanislavcapek.evidencepd.smeny;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.stanislavcapek.evidencepd.evidence.Evidence;
import cz.stanislavcapek.evidencepd.model.Mesic;
import cz.stanislavcapek.evidencepd.smeny.sluzebnici.NastavovacCasuSmeny;
import cz.stanislavcapek.evidencepd.zamestnanec.Zamestnanec;
import cz.stanislavcapek.evidencepd.model.FondPracovniDoby;
import cz.stanislavcapek.evidencepd.utils.Zaokrouhlovac;
import lombok.ToString;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * Instance třídy {@code TabulkaSmen}
 *
 * @author Stanislav Čapek
 */
@JsonIgnoreProperties({"rowCount", "columnCount", "tableModelListeners"})
@ToString
public class SmenyTableModelEvidence extends AbstractTableModel implements Evidence {


    private enum Time {
        ZACATEK,
        KONEC
    }

    private final Zaokrouhlovac zaokrouhlovac = new Zaokrouhlovac() {
    };

    private final String[] columnNames = {"den", "od", "do", "typ", "odpr. hodin",
            "noční", "víkend", "svátek", "dovolená", "neodpracované hodiny"};
    private Evidence evidence;
    private final Map<Integer, Smena> smeny;

    public SmenyTableModelEvidence(Evidence evidence) {
        this.evidence = evidence;
        this.smeny = evidence.getSmeny();
    }

    @Override
    public int getRowCount() {
        return evidence.getSmeny().size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final Smena smena = smeny.get(++rowIndex);
        if (smena.getTypSmeny() == TypSmeny.ZADNA && columnIndex != 0) {
            return "";
        }
        switch (columnIndex) {
            // den
            case 0:
                return String.valueOf(rowIndex);
            // začátek
            case 1:
                return getTime(smena, Time.ZACATEK);
            // konec
            case 2:
                return getTime(smena, Time.KONEC);
            // typ
            case 3:
                return smena.getTypSmeny().toString();
            // odpracované hodiny
            case 4:
                final double odpracovanoHodiny = smena.getPracovniDoba().getOdpracovano();
                return odpracovanoHodiny == 0 ? "" : odpracovanoHodiny;
            // noční hodiny
            case 5:
                double nocniHodiny = smena.getPriplatky().getNocni();
                return nocniHodiny == 0 ? "" : zaokrouhli(nocniHodiny);
            // víkend hodiny
            case 6:
                final double vikendoveHodiny = smena.getPriplatky().getVikend();
                return vikendoveHodiny == 0 ? "" : zaokrouhli(vikendoveHodiny);
            // svátek hodiny
            case 7:
                final double svatekHodiny = smena.getPriplatky().getSvatek();
                return svatekHodiny == 0 ? "" : zaokrouhli(svatekHodiny);
            // dovolená
            case 8:
                double hodinyDovolene = smena.getPracovniDoba().getDovolena();
                return hodinyDovolene == 0 ? "" : zaokrouhli(hodinyDovolene);
            // neodpracované hodiny
            case 9:
                final double neodpracovanoHodiny = smena.getPracovniDoba().getNeodpracovano();
                return neodpracovanoHodiny == 0 ? "" : zaokrouhli(neodpracovanoHodiny);

            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        int den = rowIndex + 1;
        final Smena smena = smeny.get(den);
        final LocalDate datum = LocalDate.of(evidence.getRok(), evidence.getMesic().getCislo(), den);

        switch (columnIndex) {
            case 1:
            case 2:
                if (columnIndex == 1) {
                    NastavovacCasuSmeny.nastavCas(smena, aValue, NastavovacCasuSmeny.TypCasu.ZACATEK);
                } else {
                    NastavovacCasuSmeny.nastavCas(smena, aValue, NastavovacCasuSmeny.TypCasu.KONEC);
                }
                break;

            case 3:
                if (!(aValue instanceof TypSmeny)) {
                    throw new IllegalStateException("Typ směny musí být třídy TypSmeny");
                }
                final ZakladniTovarnaNaSmeny tovarna = new ZakladniTovarnaNaSmeny();
                tovarna.nastavObdobi(datum);
                final Smena novaSmena = tovarna.vytvorSmenu(datum, ((TypSmeny) aValue));
                smeny.replace(den, novaSmena);
                break;
        }
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1 || columnIndex == 2 || columnIndex == 3;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
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
        return evidence.getSmeny();
    }

    private String getTime(Smena smena, Time timeTyp) {
        if (smena.getTypSmeny() == TypSmeny.ZADNA) {
            return "";
        }

        switch (timeTyp) {
            case ZACATEK:
                return smena.getZacatek().toLocalTime().toString();
            case KONEC:
                return smena.getKonec().toLocalTime().toString();
            default:
                return "invalid time";
        }
    }

    private double zaokrouhli(double toRound) {
        return zaokrouhlovac.getRoundedDouble(toRound, 2);
    }

}
