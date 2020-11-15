package cz.stanislavcapek.evidencepd.shift.servants;

import cz.stanislavcapek.evidencepd.shift.*;
import cz.stanislavcapek.evidencepd.shift.Shift;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
class DefaultPremiumPaymentsCounterTest {


    private static final PremiumPaymentsCounter POCITADLO_PRIPLATKU = new DefaultPremiumPaymentsCounter();
    private static final WorkingTimeCounter POCITADLO_PRACOVNI_DOBY = new TwelveHoursShiftWorkingTimeCounter();

    private static final LocalDate DEN_STREDA = LocalDate.of(2020, 5, 6);
    private static final LocalDate DEN_CTVRTEK = LocalDate.of(2020, 5, 7);
    private static final LocalDate DEN_PATEK = LocalDate.of(2020, 5, 8);
    private static final LocalDate DEN_SOBOTA = LocalDate.of(2020, 5, 9);
    private static final LocalDate DEN_NEDELE = LocalDate.of(2020, 5, 10);

    private static final LocalTime SEDM = LocalTime.of(7, 0);
    private static final LocalTime DEVATENACT = LocalTime.of(19, 0);
    private static final ShiftFactory TOVARNA_NA_SMENY = new DefaultShiftFactory();

    @Test
    void denniVTydnu() {
        final Shift shift = TOVARNA_NA_SMENY.createShift(DEN_CTVRTEK);
        shift.setWorkingHours(POCITADLO_PRACOVNI_DOBY.calulate(shift));
        final PremiumPayments premiumPayments = POCITADLO_PRIPLATKU.calculate(shift);

        log.debug(premiumPayments.toString());

        assertAll(() -> {
            assertEquals(0d, premiumPayments.getNight());
            assertEquals(0d, premiumPayments.getWeekend());
            assertEquals(0d, premiumPayments.getHoliday());
            assertEquals(0d, premiumPayments.getOvertime());
        });
    }

    @Test
    void nocniVTydnu() {
        final Shift shift = TOVARNA_NA_SMENY.createShift(DEN_STREDA, TypeOfShiftTwelveHours.NIGHT);
        shift.setWorkingHours(POCITADLO_PRACOVNI_DOBY.calulate(shift));
        final PremiumPayments premiumPayments = POCITADLO_PRIPLATKU.calculate(shift);

        log.debug(premiumPayments.toString());

        assertAll(() -> {
            assertEquals(8d, premiumPayments.getNight());
            assertEquals(0d, premiumPayments.getWeekend());
            assertEquals(0d, premiumPayments.getHoliday());
            assertEquals(0d, premiumPayments.getOvertime());
        });
    }

    @Test
    void denniVeSvatek() {
        final Shift shift = TOVARNA_NA_SMENY.createShift(DEN_PATEK);
        shift.setWorkingHours(POCITADLO_PRACOVNI_DOBY.calulate(shift));
        final PremiumPayments premiumPayments = POCITADLO_PRIPLATKU.calculate(shift);

        log.debug(premiumPayments.toString());

        assertAll(() -> {
            assertEquals(0d, premiumPayments.getNight());
            assertEquals(0d, premiumPayments.getWeekend());
            assertEquals(12d, premiumPayments.getHoliday());
            assertEquals(0d, premiumPayments.getOvertime());
        });
    }

    @Test
    void denniOVikendu() {
        final Shift shift = TOVARNA_NA_SMENY.createShift(DEN_SOBOTA);
        shift.setWorkingHours(POCITADLO_PRACOVNI_DOBY.calulate(shift));
        final PremiumPayments premiumPayments = POCITADLO_PRIPLATKU.calculate(shift);

        log.debug(shift.toString());
        log.debug(premiumPayments.toString());

        assertAll(() -> {
            assertEquals(0d, premiumPayments.getNight());
            assertEquals(12d, premiumPayments.getWeekend());
            assertEquals(0d, premiumPayments.getHoliday());
            assertEquals(0d, premiumPayments.getOvertime());
        });
    }

    @Test
    void nocniVPatekASvatek() {
        final Shift shift = TOVARNA_NA_SMENY.createShift(DEN_PATEK, TypeOfShiftTwelveHours.NIGHT);
        shift.setWorkingHours(POCITADLO_PRACOVNI_DOBY.calulate(shift));
        final PremiumPayments premiumPayments = POCITADLO_PRIPLATKU.calculate(shift);

        log.debug(premiumPayments.toString());

        assertAll(() -> {
            assertEquals(8d, premiumPayments.getNight());
            assertEquals(7d, premiumPayments.getWeekend());
            assertEquals(5d, premiumPayments.getHoliday());
            assertEquals(0d, premiumPayments.getOvertime());
        });
    }

    @Test
    void nocniOVikendu() {
        final Shift shift = TOVARNA_NA_SMENY.createShift(DEN_SOBOTA, TypeOfShiftTwelveHours.NIGHT);
        shift.setWorkingHours(POCITADLO_PRACOVNI_DOBY.calulate(shift));
        final PremiumPayments premiumPayments = POCITADLO_PRIPLATKU.calculate(shift);

        log.debug(premiumPayments.toString());

        assertAll(() -> {
            assertEquals(8d, premiumPayments.getNight());
            assertEquals(12d, premiumPayments.getWeekend());
            assertEquals(0d, premiumPayments.getHoliday());
            assertEquals(0d, premiumPayments.getOvertime());
        });
    }

    @Test
    void nocniVNedeli() {
        final Shift shift = TOVARNA_NA_SMENY.createShift(DEN_NEDELE, TypeOfShiftTwelveHours.NIGHT);
        shift.setWorkingHours(POCITADLO_PRACOVNI_DOBY.calulate(shift));
        final PremiumPayments premiumPayments = POCITADLO_PRIPLATKU.calculate(shift);

        log.debug(premiumPayments.toString());

        assertAll(() -> {
            assertEquals(8d, premiumPayments.getNight());
            assertEquals(5d, premiumPayments.getWeekend());
            assertEquals(0d, premiumPayments.getHoliday());
            assertEquals(0d, premiumPayments.getOvertime());
        });
    }

    @Test
    void dlouhaSmena23hodin() {
        final Shift shift = TOVARNA_NA_SMENY
                .createShift(DEN_PATEK.atTime(3, 15), DEN_SOBOTA.atTime(2, 45), TypeOfShiftTwelveHours.DAY);
        shift.setWorkingHours(POCITADLO_PRACOVNI_DOBY.calulate(shift));
        final PremiumPayments premiumPayments = POCITADLO_PRIPLATKU.calculate(shift);

        log.debug(premiumPayments.toString());

        assertAll(() -> {
            assertEquals(7.5d, premiumPayments.getNight());
            assertEquals(2.75d, premiumPayments.getWeekend());
            assertEquals(20.75d, premiumPayments.getHoliday());
            assertEquals(0d, premiumPayments.getOvertime());
        });
    }
}