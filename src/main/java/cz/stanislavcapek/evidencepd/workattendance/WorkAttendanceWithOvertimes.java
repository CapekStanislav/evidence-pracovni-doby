package cz.stanislavcapek.evidencepd.workattendance;

import cz.stanislavcapek.evidencepd.shift.Shift;

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
