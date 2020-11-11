package cz.stanislavcapek.evidencepd.smeny.sluzebnici;

import cz.stanislavcapek.evidencepd.smeny.Smena;
import cz.stanislavcapek.evidencepd.smeny.TypSmeny;
import cz.stanislavcapek.evidencepd.smeny.ZakladniTovarnaNaSmeny;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class NastavovacCasuSmenyTest {

    @Test
    void nastaveniZacatkuBehemJednohoDne() {
        final LocalDate datum = LocalDate.of(2020, 10, 19);
        final Smena smena = new ZakladniTovarnaNaSmeny()
                .vytvorSmenu(datum, TypSmeny.DENNI);
        String time = "1:00";

        final Smena novaSmena = NastavovacCasuSmeny.nastavCas(smena, time, NastavovacCasuSmeny.TypCasu.ZACATEK);

        assertNotNull(novaSmena);
        System.out.println("novaSmena = " + novaSmena);
        assertEquals(datum.atTime(LocalTime.of(1, 0)), novaSmena.getZacatek());
        assertEquals(datum.atTime(LocalTime.of(19, 0)), novaSmena.getKonec());
    }

    @Test
    void nastaveniKonceBehemJednohoDne() {
        final LocalDate datum = LocalDate.of(2020, 10, 19);
        final Smena smena = new ZakladniTovarnaNaSmeny()
                .vytvorSmenu(datum, TypSmeny.DENNI);
        String time = "12:12";

        final Smena novaSmena = NastavovacCasuSmeny.nastavCas(smena, time, NastavovacCasuSmeny.TypCasu.KONEC);

        assertNotNull(novaSmena);
        System.out.println("novaSmena = " + novaSmena);
        assertEquals(datum.atTime(LocalTime.of(7, 0)), novaSmena.getZacatek());
        assertEquals(datum.atTime(LocalTime.of(12, 12)), novaSmena.getKonec());
    }

    @Test
    void nastaveniKonceSPresahemDoDalsihoDne() {
        final LocalDate datum = LocalDate.of(2020, 10, 19);
        final Smena smena = new ZakladniTovarnaNaSmeny()
                .vytvorSmenu(datum, TypSmeny.DENNI);
        String time = "3:20";

        final Smena novaSmena = NastavovacCasuSmeny.nastavCas(smena, time, NastavovacCasuSmeny.TypCasu.KONEC);

        assertNotNull(novaSmena);
        System.out.println("novaSmena = " + novaSmena);
        assertEquals(datum.atTime(LocalTime.of(7, 0)), novaSmena.getZacatek());
        assertEquals(datum.plusDays(1).atTime(LocalTime.of(3, 20)), novaSmena.getKonec());

    }
}