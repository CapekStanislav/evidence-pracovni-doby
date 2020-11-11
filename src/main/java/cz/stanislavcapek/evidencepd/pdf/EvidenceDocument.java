package cz.stanislavcapek.evidencepd.pdf;

import cz.stanislavcapek.evidencepd.evidence.Evidence;
import cz.stanislavcapek.evidencepd.smeny.PracovniDoba;
import cz.stanislavcapek.evidencepd.smeny.Smena;
import cz.stanislavcapek.evidencepd.model.FondPracovniDoby;

import javax.swing.table.TableModel;
import java.time.LocalDate;

/**
 * An instance of class {@code EvidenceDocument}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class EvidenceDocument {

    private final TableModel model;
    private final Evidence evidence;

    public <T extends TableModel> EvidenceDocument(Evidence evidence, T model) {
        this.evidence = evidence;
        this.model = model;
    }

    public String getName() {
        return evidence.getZamestnanec().getCeleJmeno();
    }

    public int getYear() {
        return evidence.getRok();
    }

    public String getMonth() {
        return evidence.getMesic().getNazev();
    }

    /**
     * @param rowIndex 0 base index
     * @return date of shift
     */
    public LocalDate getDate(int rowIndex) {
//        return data.getDataAsList().get(rowIndex).getZacatek().toLocalDate();
        final int den = rowIndex + 1;
        return evidence.getSmeny().get(den).getZacatek().toLocalDate();
    }

    public int getColumnCount() {
        return model.getColumnCount();
    }

    public String getColumnName(int columnIndex) {
        return model.getColumnName(columnIndex);
    }

    public int getRowCount() {
        return model.getRowCount();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return model.getValueAt(rowIndex, columnIndex);
    }

    public double getWorkTimeFund() {
        return FondPracovniDoby.vypoctiFondPracovniDoby(
                getDate(0),
                evidence.getTydenniPracDoba()
        );
    }

    public double getLastMonthHours() {
        return evidence.getPredchoziMesic();
    }

    public double getNextMonthHours() {
        return (getWorkedHours() + getLastMonthHours()) - getWorkTimeFund();
    }

    public double getWorkedHours() {
        return evidence.getSmeny().values()
                .stream()
                .map(Smena::getPracovniDoba)
                .mapToDouble(PracovniDoba::getOdpracovano)
                .sum();

    }
}
