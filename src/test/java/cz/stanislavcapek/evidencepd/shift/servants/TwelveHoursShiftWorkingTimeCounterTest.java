package cz.stanislavcapek.evidencepd.shift.servants;

import cz.stanislavcapek.evidencepd.shift.*;
import cz.stanislavcapek.evidencepd.shift.Shift;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
class TwelveHoursShiftWorkingTimeCounterTest {

    private static final WorkingTimeCounter POCITADLO_PRACOVNI_DOBY = new TwelveHoursShiftWorkingTimeCounter();
    private static final LocalDate DEN_PATEK = LocalDate.of(2020, 5, 8);
    private static final LocalDate DEN_SOBOTA = LocalDate.of(2020, 5, 9);
    private static final LocalDate DEN_NEDELE = LocalDate.of(2020, 5, 9);

    private static final LocalTime SEDM = LocalTime.of(7, 0);
    private static final LocalTime DEVATENACT = LocalTime.of(19, 0);
    private static final ShiftFactory TOVARNA_NA_SMENY = new DefaultShiftFactory();

    @BeforeAll
    static void beforeAll() {
        TOVARNA_NA_SMENY.setPeriod(DEN_PATEK);
    }

    @Test
    void denniVTydnu() {
        final Shift shift = TOVARNA_NA_SMENY.createShift(DEN_PATEK);
        final WorkingTime workingTime = POCITADLO_PRACOVNI_DOBY.calulate(shift);

        log.debug(workingTime.toString());

        assertAll(() -> {
            assertEquals(workingTime.getLength(),12d);
            assertEquals(workingTime.getWorkedOut(),12d);
            assertEquals(workingTime.getNotWorkedOut(),0d);
            assertEquals(workingTime.getHoliday(),0d);
        });
    }

    @Test
    void nocniVPatek() {
        final Shift shift = TOVARNA_NA_SMENY.createShift(DEN_PATEK, TypeOfShiftTwelveHours.NIGHT);
        final WorkingTime workingTime = POCITADLO_PRACOVNI_DOBY.calulate(shift);

        log.debug(workingTime.toString());

        assertAll(() -> {
            assertEquals(workingTime.getLength(),12d);
            assertEquals(workingTime.getWorkedOut(),12d);
            assertEquals(workingTime.getNotWorkedOut(),0d);
            assertEquals(workingTime.getHoliday(),0d);
        });

    }

    @Test
    void dovolenaVTydnu() {
        final Shift shift = TOVARNA_NA_SMENY.createShift(DEN_PATEK, TypeOfShiftTwelveHours.HOLIDAY);
        final WorkingTime workingTime = POCITADLO_PRACOVNI_DOBY.calulate(shift);

        log.debug(workingTime.toString());

        assertAll(() -> {
            assertEquals(workingTime.getLength(),12d);
            assertEquals(workingTime.getWorkedOut(),0);
            assertEquals(workingTime.getNotWorkedOut(),0d);
            assertEquals(workingTime.getHoliday(),12d);
        });
    }

    @Test
    void puldenDovoleneVTydnu() {
        final Shift shift = TOVARNA_NA_SMENY.createShift(DEN_PATEK, TypeOfShiftTwelveHours.HALF_HOLIDAY);
        final WorkingTime workingTime = POCITADLO_PRACOVNI_DOBY.calulate(shift);

        log.debug(workingTime.toString());

        assertAll(() -> {
            assertEquals(workingTime.getLength(),12d);
            assertEquals(workingTime.getWorkedOut(),6d);
            assertEquals(workingTime.getNotWorkedOut(),0d);
            assertEquals(workingTime.getHoliday(),6d);
        });
    }

    @Test
    void neschopnostVTydnu() {
        final Shift shift = TOVARNA_NA_SMENY.createShift(DEN_PATEK, TypeOfShiftTwelveHours.INABILITY);
        final WorkingTime workingTime = POCITADLO_PRACOVNI_DOBY.calulate(shift);

        log.debug(workingTime.toString());

        assertAll(() -> {
            assertEquals(workingTime.getLength(),12d);
            assertEquals(workingTime.getWorkedOut(),0d);
            assertEquals(workingTime.getNotWorkedOut(),12d);
            assertEquals(workingTime.getHoliday(),0d);
        });
    }

    @Test
    void osetrovaniVTydnu() {
        final Shift shift = TOVARNA_NA_SMENY.createShift(DEN_PATEK, TypeOfShiftTwelveHours.HOME_CARE);
        final WorkingTime workingTime = POCITADLO_PRACOVNI_DOBY.calulate(shift);

        log.debug(workingTime.toString());

        assertAll(() -> {
            assertEquals(workingTime.getLength(),12d);
            assertEquals(workingTime.getWorkedOut(),0d);
            assertEquals(workingTime.getNotWorkedOut(),12d);
            assertEquals(workingTime.getHoliday(),0d);
        });
    }

    @Test
    void skoleniVTydnu() {
        final Shift shift = TOVARNA_NA_SMENY.createShift(DEN_PATEK, TypeOfShiftTwelveHours.TRAINING);
        final WorkingTime workingTime = POCITADLO_PRACOVNI_DOBY.calulate(shift);

        log.debug(workingTime.toString());

        assertAll(() -> {
            assertEquals(workingTime.getLength(),7.5d);
            assertEquals(workingTime.getWorkedOut(),7.5d);
            assertEquals(workingTime.getNotWorkedOut(),0d);
            assertEquals(workingTime.getHoliday(),0d);
        });

    }

    @Test
    void zadnaVTydnu() {
        final Shift shift = TOVARNA_NA_SMENY.createShift(DEN_PATEK, TypeOfShiftTwelveHours.NONE);
        final WorkingTime workingTime = POCITADLO_PRACOVNI_DOBY.calulate(shift);

        log.debug(workingTime.toString());

        assertAll(() -> {
            assertEquals(workingTime.getLength(),0d);
            assertEquals(workingTime.getWorkedOut(),0d);
            assertEquals(workingTime.getNotWorkedOut(),0d);
            assertEquals(workingTime.getHoliday(),0d);
        });
    }

    @Test
    void zdravotniVolnoVTydnu() {
        final Shift shift = TOVARNA_NA_SMENY.createShift(DEN_PATEK, TypeOfShiftTwelveHours.SICK_DAY);
        final WorkingTime workingTime = POCITADLO_PRACOVNI_DOBY.calulate(shift);

        log.debug(workingTime.toString());

        assertAll(() -> {
            assertEquals(workingTime.getLength(),12d);
            assertEquals(workingTime.getWorkedOut(),0d);
            assertEquals(workingTime.getNotWorkedOut(),12d);
            assertEquals(workingTime.getHoliday(),0d);
        });
    }
}