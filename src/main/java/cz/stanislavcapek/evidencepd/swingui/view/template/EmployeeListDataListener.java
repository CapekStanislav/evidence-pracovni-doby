package cz.stanislavcapek.evidencepd.swingui.view.template;

import cz.stanislavcapek.evidencepd.swingui.model.EmployeeListModel;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.List;

public class EmployeeListDataListener implements ListDataListener {

    private final List<EmployeeModelChangedListener> modelChangedListeners = new ArrayList<>();

    public void addModelChangeListener(EmployeeModelChangedListener listener) {
        modelChangedListeners.add(listener);
    }

    @Override
    public void intervalAdded(ListDataEvent listDataEvent) {
        fireModelChanged(((EmployeeListModel) listDataEvent.getSource()));
    }

    @Override
    public void intervalRemoved(ListDataEvent listDataEvent) {
        fireModelChanged(((EmployeeListModel) listDataEvent.getSource()));
    }

    @Override
    public void contentsChanged(ListDataEvent listDataEvent) {
        fireModelChanged(((EmployeeListModel) listDataEvent.getSource()));
    }

    void fireModelChanged(EmployeeListModel model) {
        SwingUtilities.invokeLater(() -> modelChangedListeners.forEach(l -> l.modelChanged(model.getEmployeeList())));
    }
}
