package cz.stanislavcapek.evidencepd.employee;

import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class EmployeeListModelProvider implements Provider<EmployeeListModel> {

    private EmployeeListModel model;

    public EmployeeListModelProvider() {
    }

    @Override
    public EmployeeListModel get() {
        if (model == null) {
            model = new EmployeeListModel();
        }
        return model;
    }
}
