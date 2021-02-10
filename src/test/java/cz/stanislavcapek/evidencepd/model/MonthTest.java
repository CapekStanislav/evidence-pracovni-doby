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
        assertEquals(Month.getNumberOfDays(Month.JANUARY, 2020), 31);
        assertEquals(Month.getNumberOfDays(Month.MARCH, 2020), 31);
        assertEquals(Month.getNumberOfDays(Month.SEPTEMBER, 2020), 30);
    }

    @Test
    void getInstanceByValueOfMonth() {
        assertEquals(Month.valueOf(1), Month.JANUARY);
        assertEquals(Month.valueOf(4), Month.APRIL);
        assertEquals(Month.valueOf(12), Month.DECEMBER);
    }

    @Test
    void getExceptionWhenIllegalMonthNumber() {
        assertThrows(IllegalArgumentException.class, () -> Month.valueOf(0));
        assertThrows(IllegalArgumentException.class, () -> Month.valueOf(13));
    }

    @Test
    void whenCorrectLocalizedNameThenCorrectNumber() {
        assertAll(() -> {
            assertEquals(1, Month.getNumberByName("leden"));
            assertEquals(2, Month.getNumberByName("únor"));
            assertEquals(3, Month.getNumberByName("Březen"));
            assertEquals(4, Month.getNumberByName("duben"));
            assertEquals(5, Month.getNumberByName("KVĚTEN"));
            assertEquals(6, Month.getNumberByName("červen"));
            assertEquals(7, Month.getNumberByName("červenec"));
            assertEquals(8, Month.getNumberByName("srPEn"));
            assertEquals(9, Month.getNumberByName("zÁŘí"));
            assertEquals(10, Month.getNumberByName("říjen"));
            assertEquals(11, Month.getNumberByName("listoPAD"));
            assertEquals(12, Month.getNumberByName("prosinec"));
        });
    }

    @Test
    void whenWrongLocalizedNameThenMinusOne () {
        assertAll(() -> {
            assertEquals(-1,Month.getNumberByName("brezen"));
            assertEquals(-1,Month.getNumberByName("unor"));
            assertEquals(-1,Month.getNumberByName("ljfsdlka"));
            assertEquals(-1,Month.getNumberByName("ZARI"));
        });
    }
}