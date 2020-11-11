package cz.stanislavcapek.evidencepd.model;

import cz.stanislavcapek.evidencepd.dao.Dao;
import cz.stanislavcapek.evidencepd.evidence.Evidence;
import cz.stanislavcapek.evidencepd.plansmen.PlanSmen;
import cz.stanislavcapek.evidencepd.plansmen.XlsxDao;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static cz.stanislavcapek.evidencepd.model.FondPracovniDoby.DruhTydenniPracDoby.VICESMENNY_NEPRETRZITY;
import static org.junit.jupiter.api.Assertions.*;

@Log4j2
class PlanSmenTest {


    private static PlanSmen plan;
    private static PlanSmen planChybny;

    @BeforeAll
    static void beforeAll() throws IOException {
        Dao<XSSFWorkbook> io = new XlsxDao();
        XSSFWorkbook workbook = io.nacti(Path.of("src/test/resources/test_4_straznici.xlsx"));
        XSSFWorkbook workbookBezStrazniku = io.nacti(Path.of("src/test/resources/test_0_straznici.xlsx"));
        plan = new PlanSmen(workbook, VICESMENNY_NEPRETRZITY);
        planChybny = new PlanSmen(workbookBezStrazniku, VICESMENNY_NEPRETRZITY);
    }

    @Test
    void ziskejRok() {
        assertEquals(plan.getRok(), 2019);
        assertNotEquals(planChybny.getRok(), 2019);
    }

    @Test
    void vyskytujeSeVMesiciZmestnanec() {
        assertTrue(plan.isZamestnanec(1, 1));
        assertFalse(plan.isZamestnanec(5, 1));
        assertFalse(planChybny.isZamestnanec(1, 1));
    }

    @Test
    void vyskytujeSeVPlanuSmenZamestnanec() {
        assertTrue(plan.isZamestnanec(1));
        assertFalse(plan.isZamestnanec(5));
        assertFalse(planChybny.isZamestnanec(2));
    }

    @Test
    void ziskejEvidenciZamestnanceZaMesic() {
        final Evidence evidence = plan.getEvidenci(1, 2);

        log.info("Evidence");
        log.info(evidence.getZamestnanec().toString());
        log.info(evidence.getMesic().toString());
        log.info(String.valueOf(evidence.getPredchoziMesic()));
        log.info(evidence.getSmeny().toString());

        assertNotNull(evidence);
    }

    @Test
    void ziskejEvidenciPrescasu() {
        final Evidence prescasy = plan.getEvidenciPrescasu(1, 2);

        log.info("Evidence přesčasů");
        log.info(prescasy.toString());

        assertNotNull(prescasy);
        assertEquals(prescasy.getSmeny().size(), 2);

    }
}