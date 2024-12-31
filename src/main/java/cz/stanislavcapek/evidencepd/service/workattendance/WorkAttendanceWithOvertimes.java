package cz.stanislavcapek.evidencepd.service.workattendance;

import cz.stanislavcapek.evidencepd.service.shiftplan.shift.Shift;

import java.util.List;

/**
 * An instance of interface {@code WorkAttendanceWithOvertimes}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public interface WorkAttendanceWithOvertimes extends WorkAttendance {

    List<Shift> getOvertimes();

}
