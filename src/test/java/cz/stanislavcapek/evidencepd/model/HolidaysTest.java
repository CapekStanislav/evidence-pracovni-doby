package cz.stanislavcapek.evidencepd.model;

import cz.stanislavcapek.evidencepd.holiday.Holidays;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
class HolidaysTest {

    @Test
    void ziskejInstanciSVatku() {
        final Holidays instance = Holidays.getInstance(2020);

        log.info(instance.toString());
        assertAll(() -> {
            assertEquals(2020, instance.getYear());
            assertNotEquals(0, instance.getDatesOfHolidays().size());
        });
    }
}