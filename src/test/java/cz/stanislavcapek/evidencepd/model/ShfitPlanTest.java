package cz.stanislavcapek.evidencepd.model;

import cz.stanislavcapek.evidencepd.dao.Dao;
import cz.stanislavcapek.evidencepd.record.Record;
import cz.stanislavcapek.evidencepd.shiftplan.ShfitPlan;
import cz.stanislavcapek.evidencepd.shiftplan.XlsxDao;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static cz.stanislavcapek.evidencepd.model.WorkingTimeFund.TypeOfWeeklyWorkingTime.MULTISHIFT_CONTINUOUS;
import static org.junit.jupiter.api.Assertions.*;

@Log4j2
class ShfitPlanTest {


    private static ShfitPlan plan;
    private static ShfitPlan planChybny;

    @BeforeAll
    static void beforeAll() throws IOException {
        Dao<XSSFWorkbook> io = new XlsxDao();
        XSSFWorkbook workbook = io.load(Path.of("src/test/resources/test_4_straznici.xlsx"));
        XSSFWorkbook workbookBezStrazniku = io.load(Path.of("src/test/resources/test_0_straznici.xlsx"));
        plan = new ShfitPlan(workbook, MULTISHIFT_CONTINUOUS);
        planChybny = new ShfitPlan(workbookBezStrazniku, MULTISHIFT_CONTINUOUS);
    }

    @Test
    void ziskejRok() {
        assertEquals(plan.getYear(), 2019);
        assertNotEquals(planChybny.getYear(), 2019);
    }

    @Test
    void vyskytujeSeVMesiciZmestnanec() {
        assertTrue(plan.isEmployee(1, 1));
        assertFalse(plan.isEmployee(5, 1));
        assertFalse(planChybny.isEmployee(1, 1));
    }

    @Test
    void vyskytujeSeVPlanuSmenZamestnanec() {
        assertTrue(plan.isEmployee(1));
        assertFalse(plan.isEmployee(5));
        assertFalse(planChybny.isEmployee(2));
    }

    @Test
    void ziskejEvidenciZamestnanceZaMesic() {
        final Record record = plan.getRecord(1, 2);

        log.info("Record");
        log.info(record.getEmployee().toString());
        log.info(record.getMonth().toString());
        log.info(String.valueOf(record.getLastMonth()));
        log.info(record.getShifts().toString());

        assertNotNull(record);
    }

    @Test
    void ziskejEvidenciPrescasu() {
        final Record prescasy = plan.getRecordOvertime(1, 2);

        log.info("Record přesčasů");
        log.info(prescasy.toString());

        assertNotNull(prescasy);
        assertEquals(prescasy.getShifts().size(), 2);

    }
}