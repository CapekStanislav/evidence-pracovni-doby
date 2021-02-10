package cz.stanislavcapek.evidencepd.model;

import cz.stanislavcapek.evidencepd.dao.Dao;
import cz.stanislavcapek.evidencepd.workattendance.WorkAttendance;
import cz.stanislavcapek.evidencepd.shiftplan.ShiftPlan;
import cz.stanislavcapek.evidencepd.shiftplan.XlsxDao;
import cz.stanislavcapek.evidencepd.workattendance.exception.WorkAttendanceNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import static cz.stanislavcapek.evidencepd.model.WorkingTimeFund.TypeOfWeeklyWorkingTime.MULTISHIFT_CONTINUOUS;
import static org.junit.jupiter.api.Assertions.*;

@Log4j2
class ShiftPlanTest {


    private static ShiftPlan plan;
    private static ShiftPlan planChybny;
    private static ShiftPlan planBezMesicu;

    @BeforeAll
    static void beforeAll() throws IOException {
        Dao<XSSFWorkbook> io = new XlsxDao();
        XSSFWorkbook workbook = io.load(Path.of("src/test/resources/test_4_straznici.xlsx"));
        XSSFWorkbook workbookBezStrazniku = io.load(Path.of("src/test/resources/test_0_straznici.xlsx"));
        final XSSFWorkbook workbookBezMesicu = io.load(Path.of("src/test/resources/test_4_straznici_bez_mesicu.xlsx"));
        plan = new ShiftPlan(workbook, MULTISHIFT_CONTINUOUS);
        planChybny = new ShiftPlan(workbookBezStrazniku, MULTISHIFT_CONTINUOUS);
        planBezMesicu = new ShiftPlan(workbookBezMesicu, MULTISHIFT_CONTINUOUS);
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
        final WorkAttendance workAttendance = plan.getWorkAttendance(1, 2);

        log.info("WorkAttendance");
        log.info(workAttendance.getEmployee().toString());
        log.info(workAttendance.getMonth().toString());
        log.info(String.valueOf(workAttendance.getLastMonth()));
        log.info(workAttendance.getShifts().toString());

        assertNotNull(workAttendance);
    }

    @Test
    void ziskejEvidenciPrescasu() {
        final WorkAttendance prescasy = plan.getWorkAttendanceOvertime(1, 2);

        log.info("WorkAttendance přesčasů");
        log.info(prescasy.toString());

        assertNotNull(prescasy);
        assertEquals(prescasy.getShifts().size(), 2);

    }

    @Test
    void ziskejEvidenciKdyzChybiNektereMesice() {
        assertNotNull(planBezMesicu);

        final int id = 1;
        final int march = 3;
        final int january = 1;

        assertFalse(planBezMesicu.isEmployee(id, january));
        assertTrue(planBezMesicu.isEmployee(id, march));

        assertNotNull(planBezMesicu.getWorkAttendance(march, id));
        assertThrows(WorkAttendanceNotFoundException.class, () -> planBezMesicu.getWorkAttendance(id, january));
        assertThrows(WorkAttendanceNotFoundException.class, () -> planBezMesicu.getWorkAttendanceByMonth(january));
        assertThrows(WorkAttendanceNotFoundException.class, () -> planBezMesicu.getWorkAttendanceOvertime(january, id));

    }

    @Test
    void ziskejMesiceKDispozici() {
        final Set<Integer> availableMonths = planBezMesicu.getAvailableMonths();

        assertNotNull(availableMonths);
        assertEquals(10,availableMonths.size());

        assertFalse(availableMonths.contains(1));
        assertFalse(availableMonths.contains(2));

        assertTrue(availableMonths.contains(3));
        assertTrue(availableMonths.contains(4));
        assertTrue(availableMonths.contains(5));
        assertTrue(availableMonths.contains(6));
        assertTrue(availableMonths.contains(7));
        assertTrue(availableMonths.contains(8));
        assertTrue(availableMonths.contains(9));
        assertTrue(availableMonths.contains(10));
        assertTrue(availableMonths.contains(11));
        assertTrue(availableMonths.contains(12));
    }
}