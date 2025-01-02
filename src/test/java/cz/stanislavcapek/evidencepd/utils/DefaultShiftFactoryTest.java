package cz.stanislavcapek.evidencepd.utils;

import cz.stanislavcapek.evidencepd.service.shiftplan.shift.DefaultShiftFactory;
import cz.stanislavcapek.evidencepd.service.shiftplan.shift.Shift;
import cz.stanislavcapek.evidencepd.service.shiftplan.shift.TwelveHourShiftType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class DefaultShiftFactoryTest {

    private static final LocalDate DATE = LocalDate.of(2020, 10, 5);
    private static final LocalTime SEVEN = LocalTime.of(7, 0);
    private static final LocalTime NINETEEN = LocalTime.of(19, 0);

    @Test
    void vytvorSmenuPouzeSDatumem() {
        final DefaultShiftFactory factory = new DefaultShiftFactory();
        final Shift shift = factory.createShift(DATE);
        assertAll(() -> {
            assertNotNull(shift.getStart());
            assertNotNull(shift.getEnd());
            assertNotNull(shift.getWorkingHours());
            assertNotNull(shift.getPremiumPayments());
            assertNotNull(shift.getTypeOfShiftTwelveHours());
        });
        assertAll(() -> {
            assertEquals(SEVEN, shift.getStart().toLocalTime());
            assertEquals(NINETEEN, shift.getEnd().toLocalTime());
            assertEquals(TwelveHourShiftType.DAY, shift.getTypeOfShiftTwelveHours());
        });
    }

    @Test
    void testVytvorSmenuSDatumemATypemSmeny() {
        final DefaultShiftFactory tovarna = new DefaultShiftFactory();
        final Shift shift = tovarna.createShift(DATE, TwelveHourShiftType.NIGHT);
        assertAll(() -> {
            assertNotNull(shift.getStart());
            assertNotNull(shift.getEnd());
            assertNotNull(shift.getWorkingHours());
            assertNotNull(shift.getPremiumPayments());
            assertNotNull(shift.getTypeOfShiftTwelveHours());
        });

        assertAll(() -> {
            assertEquals(NINETEEN, shift.getStart().toLocalTime());
            assertEquals(SEVEN, shift.getEnd().toLocalTime());
            assertEquals(TwelveHourShiftType.NIGHT, shift.getTypeOfShiftTwelveHours());
        });
    }

    @Test
    void testVytvorSmenuSDatumemADelkou() {
        final DefaultShiftFactory tovarna = new DefaultShiftFactory();
        final Shift shift = tovarna.createShift(DATE, 7.5);
        assertAll(() -> {
            assertNotNull(shift.getStart());
            assertNotNull(shift.getEnd());
            assertNotNull(shift.getWorkingHours());
            assertNotNull(shift.getPremiumPayments());
            assertNotNull(shift.getTypeOfShiftTwelveHours());
        });
        assertAll(() -> {
            assertEquals(SEVEN, shift.getStart().toLocalTime());
            assertEquals(SEVEN.plusHours(7).plusMinutes(30), shift.getEnd().toLocalTime());
            assertEquals(TwelveHourShiftType.DAY, shift.getTypeOfShiftTwelveHours());
        });
    }

    @Test
    void testVytvorSmenuSeZacatkemKoncem() {
        final DefaultShiftFactory tovarna = new DefaultShiftFactory();
        final LocalTime st = LocalTime.of(22, 0);
        final LocalTime et = LocalTime.of(10, 0);

        final Shift shift = tovarna.createShift(
                LocalDateTime.of(DATE, st),
                LocalDateTime.of(DATE, et).plusDays(1),
                TwelveHourShiftType.NIGHT
        );
        assertAll(() -> {
            assertNotNull(shift.getStart());
            assertNotNull(shift.getEnd());
            assertNotNull(shift.getWorkingHours());
            assertNotNull(shift.getPremiumPayments());
            assertNotNull(shift.getTypeOfShiftTwelveHours());
        });

        assertAll(() -> {
            assertEquals(st, shift.getStart().toLocalTime());
            assertEquals(et, shift.getEnd().toLocalTime());
            assertEquals(TwelveHourShiftType.NIGHT, shift.getTypeOfShiftTwelveHours());
        });
    }
}