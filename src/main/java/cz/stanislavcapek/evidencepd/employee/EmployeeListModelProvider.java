package cz.stanislavcapek.evidencepd.employee;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class EmployeeListModelProvider implements Provider<EmployeeListModel> {

    private final EmployeeService service;
    private EmployeeListModel model;

    @Inject
    public EmployeeListModelProvider(EmployeeService service) {
        this.service = service;
    }

    @Override
    public EmployeeListModel get() {
        if (model == null) {
            model = new EmployeeListModel(service);
        }
        return model;
    }
}
