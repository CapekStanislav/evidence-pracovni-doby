package cz.stanislavcapek.evidencepd.model;

import cz.stanislavcapek.evidencepd.service.holiday.Holidays;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HolidaysTest {

    @Test
    void ziskejInstanciSVatku() {
        final Holidays instance = Holidays.getInstance(2020);

        assertAll(() -> {
            assertEquals(2020, instance.getYear());
            assertNotEquals(0, instance.getDatesOfHolidays().size());
        });
    }
}