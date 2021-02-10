package cz.stanislavcapek.evidencepd.dao;

import cz.stanislavcapek.evidencepd.workattendance.WorkAttendance;
import cz.stanislavcapek.evidencepd.workattendance.WorkAttendanceDao;
import cz.stanislavcapek.evidencepd.shiftplan.ShiftPlan;
import cz.stanislavcapek.evidencepd.shiftplan.XlsxDao;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static cz.stanislavcapek.evidencepd.model.WorkingTimeFund.TypeOfWeeklyWorkingTime.MULTISHIFT_CONTINUOUS;

class WorkAttendanceDaoTest {


    private static ShiftPlan PLAN;

    @BeforeAll
    static void beforeAll() throws IOException {
        Dao<XSSFWorkbook> io = new XlsxDao();
        XSSFWorkbook workbook = io.load(Path.of("src/test/resources/test_4_straznici.xlsx"));
        PLAN = new ShiftPlan(workbook, MULTISHIFT_CONTINUOUS);
    }

    @Test
    void uloz() {
        final WorkAttendance workAttendance = PLAN.getWorkAttendance(1, 2);
        final WorkAttendanceDao dao = new WorkAttendanceDao();
        final Path path = Paths.get("src/test/resources/evidenceUlozitTest.json");
        try {
            dao.save(path, List.of(workAttendance));
            assert true;
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    void nacti() {
        final WorkAttendanceDao dao = new WorkAttendanceDao();
        final Path path = Paths.get("src/test/resources/evidenceNacistTest.json");

        try {
            final List<WorkAttendance> workAttendanceList = dao.load(path);
            assert true;
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }


    }
}