package cz.stanislavcapek.evidencepd.service.workattendance;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.service.shiftplan.ShiftsByMonth;

public class WorkAttendanceFactory {

    @Inject
    public WorkAttendanceFactory() {
    }

    public WorkAttendance create(ShiftsByMonth shiftsByMonth) {
        throw new RuntimeException("Not implemented yet.");
    }
}
