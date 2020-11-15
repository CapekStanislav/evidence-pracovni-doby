package cz.stanislavcapek.evidencepd.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MonthTest {

    @Test
    void getNazev() {
        assertEquals(Month.JANUARY.getName(), "leden");
        assertEquals(Month.MARCH.getName(), "březen");
        assertEquals(Month.SEPTEMBER.getName(), "září");
    }

    @Test
    void getCislo() {
        assertEquals(Month.JANUARY.getNumber(), 1);
        assertEquals(Month.MARCH.getNumber(), 3);
        assertEquals(Month.SEPTEMBER.getNumber(), 9);
    }


    @Test
    void getPocetDniVMesici() {
        assertEquals(Month.getNumberOfDays(Month.JANUARY,2020), 31);
        assertEquals(Month.getNumberOfDays(Month.MARCH,2020), 31);
        assertEquals(Month.getNumberOfDays(Month.SEPTEMBER,2020), 30);
    }

    @Test
    void getInstanceByValueOfMonth() {
        assertEquals(Month.valueOf(1), Month.JANUARY);
        assertEquals(Month.valueOf(4), Month.APRIL);
        assertEquals(Month.valueOf(12), Month.DECEMBER);
    }

    @Test
    void getExceptionWhenIllegalMonthNumber() {
        assertThrows(IllegalArgumentException.class,() -> Month.valueOf(0));
        assertThrows(IllegalArgumentException.class, () -> Month.valueOf(13));
    }
}