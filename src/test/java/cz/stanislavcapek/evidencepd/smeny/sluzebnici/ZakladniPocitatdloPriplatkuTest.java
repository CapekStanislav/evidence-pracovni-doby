package cz.stanislavcapek.evidencepd.smeny.sluzebnici;

import cz.stanislavcapek.evidencepd.smeny.Priplatky;
import cz.stanislavcapek.evidencepd.smeny.Smena;
import cz.stanislavcapek.evidencepd.smeny.TypSmeny;
import cz.stanislavcapek.evidencepd.smeny.TovarnaNaSmeny;
import cz.stanislavcapek.evidencepd.smeny.ZakladniTovarnaNaSmeny;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
class ZakladniPocitatdloPriplatkuTest {


    private static final PocitadloPriplatku POCITADLO_PRIPLATKU = new ZakladniPocitatdloPriplatku();
    private static final PocitadloPracovniDoby POCITADLO_PRACOVNI_DOBY = new DvanactiHodSmenyPocitadloPracovniDoby();

    private static final LocalDate DEN_STREDA = LocalDate.of(2020, 5, 6);
    private static final LocalDate DEN_CTVRTEK = LocalDate.of(2020, 5, 7);
    private static final LocalDate DEN_PATEK = LocalDate.of(2020, 5, 8);
    private static final LocalDate DEN_SOBOTA = LocalDate.of(2020, 5, 9);
    private static final LocalDate DEN_NEDELE = LocalDate.of(2020, 5, 10);

    private static final LocalTime SEDM = LocalTime.of(7, 0);
    private static final LocalTime DEVATENACT = LocalTime.of(19, 0);
    private static final TovarnaNaSmeny TOVARNA_NA_SMENY = new ZakladniTovarnaNaSmeny();

    @Test
    void denniVTydnu() {
        final Smena smena = TOVARNA_NA_SMENY.vytvorSmenu(DEN_CTVRTEK);
        smena.setPracovniDoba(POCITADLO_PRACOVNI_DOBY.vypoctiPracovniDobu(smena));
        final Priplatky priplatky = POCITADLO_PRIPLATKU.vypoctiPriplatky(smena);

        log.debug(priplatky.toString());

        assertAll(() -> {
            assertEquals(0d, priplatky.getNocni());
            assertEquals(0d, priplatky.getVikend());
            assertEquals(0d, priplatky.getSvatek());
            assertEquals(0d, priplatky.getPrescas());
        });
    }

    @Test
    void nocniVTydnu() {
        final Smena smena = TOVARNA_NA_SMENY.vytvorSmenu(DEN_STREDA, TypSmeny.NOCNI);
        smena.setPracovniDoba(POCITADLO_PRACOVNI_DOBY.vypoctiPracovniDobu(smena));
        final Priplatky priplatky = POCITADLO_PRIPLATKU.vypoctiPriplatky(smena);

        log.debug(priplatky.toString());

        assertAll(() -> {
            assertEquals(8d, priplatky.getNocni());
            assertEquals(0d, priplatky.getVikend());
            assertEquals(0d, priplatky.getSvatek());
            assertEquals(0d, priplatky.getPrescas());
        });
    }

    @Test
    void denniVeSvatek() {
        final Smena smena = TOVARNA_NA_SMENY.vytvorSmenu(DEN_PATEK);
        smena.setPracovniDoba(POCITADLO_PRACOVNI_DOBY.vypoctiPracovniDobu(smena));
        final Priplatky priplatky = POCITADLO_PRIPLATKU.vypoctiPriplatky(smena);

        log.debug(priplatky.toString());

        assertAll(() -> {
            assertEquals(0d, priplatky.getNocni());
            assertEquals(0d, priplatky.getVikend());
            assertEquals(12d, priplatky.getSvatek());
            assertEquals(0d, priplatky.getPrescas());
        });
    }

    @Test
    void denniOVikendu() {
        final Smena smena = TOVARNA_NA_SMENY.vytvorSmenu(DEN_SOBOTA);
        smena.setPracovniDoba(POCITADLO_PRACOVNI_DOBY.vypoctiPracovniDobu(smena));
        final Priplatky priplatky = POCITADLO_PRIPLATKU.vypoctiPriplatky(smena);

        log.debug(smena.toString());
        log.debug(priplatky.toString());

        assertAll(() -> {
            assertEquals(0d, priplatky.getNocni());
            assertEquals(12d, priplatky.getVikend());
            assertEquals(0d, priplatky.getSvatek());
            assertEquals(0d, priplatky.getPrescas());
        });
    }

    @Test
    void nocniVPatekASvatek() {
        final Smena smena = TOVARNA_NA_SMENY.vytvorSmenu(DEN_PATEK, TypSmeny.NOCNI);
        smena.setPracovniDoba(POCITADLO_PRACOVNI_DOBY.vypoctiPracovniDobu(smena));
        final Priplatky priplatky = POCITADLO_PRIPLATKU.vypoctiPriplatky(smena);

        log.debug(priplatky.toString());

        assertAll(() -> {
            assertEquals(8d, priplatky.getNocni());
            assertEquals(7d, priplatky.getVikend());
            assertEquals(5d, priplatky.getSvatek());
            assertEquals(0d, priplatky.getPrescas());
        });
    }

    @Test
    void nocniOVikendu() {
        final Smena smena = TOVARNA_NA_SMENY.vytvorSmenu(DEN_SOBOTA, TypSmeny.NOCNI);
        smena.setPracovniDoba(POCITADLO_PRACOVNI_DOBY.vypoctiPracovniDobu(smena));
        final Priplatky priplatky = POCITADLO_PRIPLATKU.vypoctiPriplatky(smena);

        log.debug(priplatky.toString());

        assertAll(() -> {
            assertEquals(8d, priplatky.getNocni());
            assertEquals(12d, priplatky.getVikend());
            assertEquals(0d, priplatky.getSvatek());
            assertEquals(0d, priplatky.getPrescas());
        });
    }

    @Test
    void nocniVNedeli() {
        final Smena smena = TOVARNA_NA_SMENY.vytvorSmenu(DEN_NEDELE, TypSmeny.NOCNI);
        smena.setPracovniDoba(POCITADLO_PRACOVNI_DOBY.vypoctiPracovniDobu(smena));
        final Priplatky priplatky = POCITADLO_PRIPLATKU.vypoctiPriplatky(smena);

        log.debug(priplatky.toString());

        assertAll(() -> {
            assertEquals(8d, priplatky.getNocni());
            assertEquals(5d, priplatky.getVikend());
            assertEquals(0d, priplatky.getSvatek());
            assertEquals(0d, priplatky.getPrescas());
        });
    }

    @Test
    void dlouhaSmena23hodin() {
        final Smena smena = TOVARNA_NA_SMENY
                .vytvorSmenu(DEN_PATEK.atTime(3, 15), DEN_SOBOTA.atTime(2, 45), TypSmeny.DENNI);
        smena.setPracovniDoba(POCITADLO_PRACOVNI_DOBY.vypoctiPracovniDobu(smena));
        final Priplatky priplatky = POCITADLO_PRIPLATKU.vypoctiPriplatky(smena);

        log.debug(priplatky.toString());

        assertAll(() -> {
            assertEquals(7.5d, priplatky.getNocni());
            assertEquals(2.75d, priplatky.getVikend());
            assertEquals(20.75d, priplatky.getSvatek());
            assertEquals(0d, priplatky.getPrescas());
        });
    }
}