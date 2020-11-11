package cz.stanislavcapek.evidencepd.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MesicTest {

    @Test
    void getNazev() {
        assertEquals(Mesic.LEDEN.getNazev(), "leden");
        assertEquals(Mesic.BREZEN.getNazev(), "březen");
        assertEquals(Mesic.ZARI.getNazev(), "září");
    }

    @Test
    void getCislo() {
        assertEquals(Mesic.LEDEN.getCislo(), 1);
        assertEquals(Mesic.BREZEN.getCislo(), 3);
        assertEquals(Mesic.ZARI.getCislo(), 9);
    }


    @Test
    void getPocetDniVMesici() {
        assertEquals(Mesic.getPocetDniVMesici(Mesic.LEDEN,2020), 31);
        assertEquals(Mesic.getPocetDniVMesici(Mesic.BREZEN,2020), 31);
        assertEquals(Mesic.getPocetDniVMesici(Mesic.ZARI,2020), 30);
    }

    @Test
    void getInstanceByValueOfMonth() {
        assertEquals(Mesic.valueOf(1),Mesic.LEDEN);
        assertEquals(Mesic.valueOf(4),Mesic.DUBEN);
        assertEquals(Mesic.valueOf(12),Mesic.PROSINEC);
    }

    @Test
    void getExceptionWhenIllegalMonthNumber() {
        assertThrows(IllegalArgumentException.class,() -> Mesic.valueOf(0));
        assertThrows(IllegalArgumentException.class, () -> Mesic.valueOf(13));
    }
}