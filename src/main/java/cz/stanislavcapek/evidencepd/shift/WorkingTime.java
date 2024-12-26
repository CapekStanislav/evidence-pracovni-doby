package cz.stanislavcapek.evidencepd.shift;

/**
 * An instance of class {@code WorkingTime}
 */
public class WorkingTime {
    private double length;
    private double workedOut;
    private double notWorkedOut;
    private double holiday;

    public WorkingTime(double length,
                       double workedOut,
                       double notWorkedOut,
                       double holiday) {
        this.length = length;
        this.workedOut = workedOut;
        this.notWorkedOut = notWorkedOut;
        this.holiday = holiday;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWorkedOut() {
        return workedOut;
    }

    public void setWorkedOut(double workedOut) {
        this.workedOut = workedOut;
    }

    public double getNotWorkedOut() {
        return notWorkedOut;
    }

    public void setNotWorkedOut(double notWorkedOut) {
        this.notWorkedOut = notWorkedOut;
    }

    public double getHoliday() {
        return holiday;
    }

    public void setHoliday(double holiday) {
        this.holiday = holiday;
    }
}
