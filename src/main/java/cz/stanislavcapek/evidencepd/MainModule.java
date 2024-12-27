package cz.stanislavcapek.evidencepd;

import com.google.inject.AbstractModule;
import cz.stanislavcapek.evidencepd.ui.component.WorkAttendanceLoadPanel;
import cz.stanislavcapek.evidencepd.ui.component.WorkAttendanceLoadPanelProvider;

public class MainModule extends AbstractModule {

    @Override
    public void configure() {
        bind(WorkAttendanceLoadPanel.class).toProvider(WorkAttendanceLoadPanelProvider.class);
    }

}
