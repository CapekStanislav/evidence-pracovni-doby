package cz.stanislavcapek.evidencepd.service.shiftplan.shift;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An instance of class {@code WorkingTime}
 */
public class WorkingTime {
    @JsonProperty("length")
    private double length;
    @JsonProperty("workedOut")
    private double workedOut;
    @JsonProperty("notWorkedOut")
    private double notWorkedOut;
    @JsonProperty("holiday")
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
