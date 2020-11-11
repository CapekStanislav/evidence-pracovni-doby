package cz.stanislavcapek.evidencepd.smeny.sluzebnici;

import cz.stanislavcapek.evidencepd.smeny.PracovniDoba;
import cz.stanislavcapek.evidencepd.smeny.Smena;
import cz.stanislavcapek.evidencepd.smeny.TypSmeny;
import cz.stanislavcapek.evidencepd.smeny.TovarnaNaSmeny;
import cz.stanislavcapek.evidencepd.smeny.ZakladniTovarnaNaSmeny;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
class DvanactiHodSmenyPocitadloPracovniDobyTest {

    private static final PocitadloPracovniDoby POCITADLO_PRACOVNI_DOBY = new DvanactiHodSmenyPocitadloPracovniDoby();
    private static final LocalDate DEN_PATEK = LocalDate.of(2020, 5, 8);
    private static final LocalDate DEN_SOBOTA = LocalDate.of(2020, 5, 9);
    private static final LocalDate DEN_NEDELE = LocalDate.of(2020, 5, 9);

    private static final LocalTime SEDM = LocalTime.of(7, 0);
    private static final LocalTime DEVATENACT = LocalTime.of(19, 0);
    private static final TovarnaNaSmeny TOVARNA_NA_SMENY = new ZakladniTovarnaNaSmeny();

    @BeforeAll
    static void beforeAll() {
        TOVARNA_NA_SMENY.nastavObdobi(DEN_PATEK);
    }

    @Test
    void denniVTydnu() {
        final Smena smena = TOVARNA_NA_SMENY.vytvorSmenu(DEN_PATEK);
        final PracovniDoba pracovniDoba = POCITADLO_PRACOVNI_DOBY.vypoctiPracovniDobu(smena);

        log.debug(pracovniDoba.toString());

        assertAll(() -> {
            assertEquals(pracovniDoba.getDelka(),12d);
            assertEquals(pracovniDoba.getOdpracovano(),12d);
            assertEquals(pracovniDoba.getNeodpracovano(),0d);
            assertEquals(pracovniDoba.getDovolena(),0d);
        });
    }

    @Test
    void nocniVPatek() {
        final Smena smena = TOVARNA_NA_SMENY.vytvorSmenu(DEN_PATEK, TypSmeny.NOCNI);
        final PracovniDoba pracovniDoba = POCITADLO_PRACOVNI_DOBY.vypoctiPracovniDobu(smena);

        log.debug(pracovniDoba.toString());

        assertAll(() -> {
            assertEquals(pracovniDoba.getDelka(),12d);
            assertEquals(pracovniDoba.getOdpracovano(),12d);
            assertEquals(pracovniDoba.getNeodpracovano(),0d);
            assertEquals(pracovniDoba.getDovolena(),0d);
        });

    }

    @Test
    void dovolenaVTydnu() {
        final Smena smena = TOVARNA_NA_SMENY.vytvorSmenu(DEN_PATEK,TypSmeny.DOVOLENA);
        final PracovniDoba pracovniDoba = POCITADLO_PRACOVNI_DOBY.vypoctiPracovniDobu(smena);

        log.debug(pracovniDoba.toString());

        assertAll(() -> {
            assertEquals(pracovniDoba.getDelka(),12d);
            assertEquals(pracovniDoba.getOdpracovano(),0);
            assertEquals(pracovniDoba.getNeodpracovano(),0d);
            assertEquals(pracovniDoba.getDovolena(),12d);
        });
    }

    @Test
    void puldenDovoleneVTydnu() {
        final Smena smena = TOVARNA_NA_SMENY.vytvorSmenu(DEN_PATEK,TypSmeny.PULDENNI_DOVOLENA);
        final PracovniDoba pracovniDoba = POCITADLO_PRACOVNI_DOBY.vypoctiPracovniDobu(smena);

        log.debug(pracovniDoba.toString());

        assertAll(() -> {
            assertEquals(pracovniDoba.getDelka(),12d);
            assertEquals(pracovniDoba.getOdpracovano(),6d);
            assertEquals(pracovniDoba.getNeodpracovano(),0d);
            assertEquals(pracovniDoba.getDovolena(),6d);
        });
    }

    @Test
    void neschopnostVTydnu() {
        final Smena smena = TOVARNA_NA_SMENY.vytvorSmenu(DEN_PATEK,TypSmeny.NESCHOPNOST);
        final PracovniDoba pracovniDoba = POCITADLO_PRACOVNI_DOBY.vypoctiPracovniDobu(smena);

        log.debug(pracovniDoba.toString());

        assertAll(() -> {
            assertEquals(pracovniDoba.getDelka(),12d);
            assertEquals(pracovniDoba.getOdpracovano(),0d);
            assertEquals(pracovniDoba.getNeodpracovano(),12d);
            assertEquals(pracovniDoba.getDovolena(),0d);
        });
    }

    @Test
    void osetrovaniVTydnu() {
        final Smena smena = TOVARNA_NA_SMENY.vytvorSmenu(DEN_PATEK,TypSmeny.OSETROVANI);
        final PracovniDoba pracovniDoba = POCITADLO_PRACOVNI_DOBY.vypoctiPracovniDobu(smena);

        log.debug(pracovniDoba.toString());

        assertAll(() -> {
            assertEquals(pracovniDoba.getDelka(),12d);
            assertEquals(pracovniDoba.getOdpracovano(),0d);
            assertEquals(pracovniDoba.getNeodpracovano(),12d);
            assertEquals(pracovniDoba.getDovolena(),0d);
        });
    }

    @Test
    void skoleniVTydnu() {
        final Smena smena = TOVARNA_NA_SMENY.vytvorSmenu(DEN_PATEK,TypSmeny.SKOLENI);
        final PracovniDoba pracovniDoba = POCITADLO_PRACOVNI_DOBY.vypoctiPracovniDobu(smena);

        log.debug(pracovniDoba.toString());

        assertAll(() -> {
            assertEquals(pracovniDoba.getDelka(),7.5d);
            assertEquals(pracovniDoba.getOdpracovano(),7.5d);
            assertEquals(pracovniDoba.getNeodpracovano(),0d);
            assertEquals(pracovniDoba.getDovolena(),0d);
        });

    }

    @Test
    void zadnaVTydnu() {
        final Smena smena = TOVARNA_NA_SMENY.vytvorSmenu(DEN_PATEK,TypSmeny.ZADNA);
        final PracovniDoba pracovniDoba = POCITADLO_PRACOVNI_DOBY.vypoctiPracovniDobu(smena);

        log.debug(pracovniDoba.toString());

        assertAll(() -> {
            assertEquals(pracovniDoba.getDelka(),0d);
            assertEquals(pracovniDoba.getOdpracovano(),0d);
            assertEquals(pracovniDoba.getNeodpracovano(),0d);
            assertEquals(pracovniDoba.getDovolena(),0d);
        });
    }

    @Test
    void zdravotniVolnoVTydnu() {
        final Smena smena = TOVARNA_NA_SMENY.vytvorSmenu(DEN_PATEK,TypSmeny.ZDRAV_VOLNO);
        final PracovniDoba pracovniDoba = POCITADLO_PRACOVNI_DOBY.vypoctiPracovniDobu(smena);

        log.debug(pracovniDoba.toString());

        assertAll(() -> {
            assertEquals(pracovniDoba.getDelka(),12d);
            assertEquals(pracovniDoba.getOdpracovano(),0d);
            assertEquals(pracovniDoba.getNeodpracovano(),12d);
            assertEquals(pracovniDoba.getDovolena(),0d);
        });
    }
}