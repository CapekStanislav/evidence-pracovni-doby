package cz.stanislavcapek.evidencepd.view.component.util;

import cz.stanislavcapek.evidencepd.zamestnanec.Zamestnanec;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Component;

/**
 * An instance of class {@code ZamestnanecRenderer}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class ZamestnanecRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        final Zamestnanec zamestnanec = value instanceof Zamestnanec ? ((Zamestnanec) value) : null;
        setText(String.format("%s - %s", zamestnanec.getId(), zamestnanec.getCeleJmeno()));
        return this;
    }
}
