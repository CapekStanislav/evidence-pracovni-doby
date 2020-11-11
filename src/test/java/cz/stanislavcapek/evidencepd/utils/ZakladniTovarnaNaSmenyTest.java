package cz.stanislavcapek.evidencepd.utils;

import cz.stanislavcapek.evidencepd.smeny.Smena;
import cz.stanislavcapek.evidencepd.smeny.TypSmeny;
import cz.stanislavcapek.evidencepd.smeny.ZakladniTovarnaNaSmeny;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

    @Log4j2
class ZakladniTovarnaNaSmenyTest {

    private final LocalDate datum = LocalDate.of(2020, 10, 5);
    private final LocalTime sedm = LocalTime.of(7, 0);
    private final LocalTime devatenact = LocalTime.of(19, 0);

    @Test
    void vytvorSmenuPouzeSDatumem() {
        final ZakladniTovarnaNaSmeny tovarna = new ZakladniTovarnaNaSmeny();
        final Smena smena = tovarna.vytvorSmenu(datum);
        log.debug(smena.toString());
        assertAll(() -> {
            assertNotNull(smena.getZacatek());
            assertNotNull(smena.getKonec());
            assertNotNull(smena.getPracovniDoba());
            assertNotNull(smena.getPriplatky());
            assertNotNull(smena.getTypSmeny());
        });
        assertAll(() -> {
            assertEquals(sedm, smena.getZacatek().toLocalTime());
            assertEquals(devatenact, smena.getKonec().toLocalTime());
            assertEquals(TypSmeny.DENNI, smena.getTypSmeny());
        });
    }

    @Test
    void testVytvorSmenuSDatumemATypemSmeny() {
        final ZakladniTovarnaNaSmeny tovarna = new ZakladniTovarnaNaSmeny();
        final Smena smena = tovarna.vytvorSmenu(datum, TypSmeny.NOCNI);
        log.debug(smena.toString());
        assertAll(() -> {
            assertNotNull(smena.getZacatek());
            assertNotNull(smena.getKonec());
            assertNotNull(smena.getPracovniDoba());
            assertNotNull(smena.getPriplatky());
            assertNotNull(smena.getTypSmeny());
        });

        assertAll(() -> {
            assertEquals(devatenact, smena.getZacatek().toLocalTime());
            assertEquals(sedm, smena.getKonec().toLocalTime());
            assertEquals(TypSmeny.NOCNI, smena.getTypSmeny());
        });
    }

    @Test
    void testVytvorSmenuSDatumemADelkou() {
        final ZakladniTovarnaNaSmeny tovarna = new ZakladniTovarnaNaSmeny();
        final Smena smena = tovarna.vytvorSmenu(datum, 7.5);
        log.debug(smena.toString());
        assertAll(() -> {
            assertNotNull(smena.getZacatek());
            assertNotNull(smena.getKonec());
            assertNotNull(smena.getPracovniDoba());
            assertNotNull(smena.getPriplatky());
            assertNotNull(smena.getTypSmeny());
        });
        assertAll(() -> {
            assertEquals(sedm, smena.getZacatek().toLocalTime());
            assertEquals(sedm.plusHours(7).plusMinutes(30), smena.getKonec().toLocalTime());
            assertEquals(TypSmeny.DENNI, smena.getTypSmeny());
        });
    }

    @Test
    void testVytvorSmenuSeZacatkemKoncem() {
        final ZakladniTovarnaNaSmeny tovarna = new ZakladniTovarnaNaSmeny();
        final LocalTime st = LocalTime.of(22, 0);
        final LocalTime et = LocalTime.of(10, 0);

        final Smena smena = tovarna.vytvorSmenu(
                LocalDateTime.of(datum, st),
                LocalDateTime.of(datum, et).plusDays(1),
                TypSmeny.NOCNI
        );
        log.debug(smena.toString());
        assertAll(() -> {
            assertNotNull(smena.getZacatek());
            assertNotNull(smena.getKonec());
            assertNotNull(smena.getPracovniDoba());
            assertNotNull(smena.getPriplatky());
            assertNotNull(smena.getTypSmeny());
        });

        assertAll(() -> {
            assertEquals(st, smena.getZacatek().toLocalTime());
            assertEquals(et, smena.getKonec().toLocalTime());
            assertEquals(TypSmeny.NOCNI, smena.getTypSmeny());
        });
    }
}