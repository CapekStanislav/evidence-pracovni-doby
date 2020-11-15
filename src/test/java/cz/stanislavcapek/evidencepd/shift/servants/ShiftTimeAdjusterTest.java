package cz.stanislavcapek.evidencepd.shift.servants;

import cz.stanislavcapek.evidencepd.shift.Shift;
import cz.stanislavcapek.evidencepd.shift.TypeOfShiftTwelveHours;
import cz.stanislavcapek.evidencepd.shift.DefaultShiftFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ShiftTimeAdjusterTest {

    @Test
    void nastaveniZacatkuBehemJednohoDne() {
        final LocalDate datum = LocalDate.of(2020, 10, 19);
        final Shift shift = new DefaultShiftFactory()
                .createShift(datum, TypeOfShiftTwelveHours.DAY);
        String time = "1:00";

        final Shift novaShift = ShiftTimeAdjuster.adjustTime(shift, time, ShiftTimeAdjuster.TimeType.START);

        assertNotNull(novaShift);
        System.out.println("novaShift = " + novaShift);
        assertEquals(datum.atTime(LocalTime.of(1, 0)), novaShift.getStart());
        assertEquals(datum.atTime(LocalTime.of(19, 0)), novaShift.getEnd());
    }

    @Test
    void nastaveniKonceBehemJednohoDne() {
        final LocalDate datum = LocalDate.of(2020, 10, 19);
        final Shift shift = new DefaultShiftFactory()
                .createShift(datum, TypeOfShiftTwelveHours.DAY);
        String time = "12:12";

        final Shift novaShift = ShiftTimeAdjuster.adjustTime(shift, time, ShiftTimeAdjuster.TimeType.END);

        assertNotNull(novaShift);
        System.out.println("novaShift = " + novaShift);
        assertEquals(datum.atTime(LocalTime.of(7, 0)), novaShift.getStart());
        assertEquals(datum.atTime(LocalTime.of(12, 12)), novaShift.getEnd());
    }

    @Test
    void nastaveniKonceSPresahemDoDalsihoDne() {
        final LocalDate datum = LocalDate.of(2020, 10, 19);
        final Shift shift = new DefaultShiftFactory()
                .createShift(datum, TypeOfShiftTwelveHours.DAY);
        String time = "3:20";

        final Shift novaShift = ShiftTimeAdjuster.adjustTime(shift, time, ShiftTimeAdjuster.TimeType.END);

        assertNotNull(novaShift);
        System.out.println("novaShift = " + novaShift);
        assertEquals(datum.atTime(LocalTime.of(7, 0)), novaShift.getStart());
        assertEquals(datum.plusDays(1).atTime(LocalTime.of(3, 20)), novaShift.getEnd());

    }
}