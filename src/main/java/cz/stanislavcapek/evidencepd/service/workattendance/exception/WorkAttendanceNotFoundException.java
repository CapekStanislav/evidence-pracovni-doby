package cz.stanislavcapek.evidencepd.service.workattendance.exception;

/**
 * An instance of class {@code WorkAttendanceNotFoundException}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class WorkAttendanceNotFoundException extends RuntimeException {

    public WorkAttendanceNotFoundException(int monthNumber) {
        super("No work attendance found for month: " + monthNumber);
    }
}
