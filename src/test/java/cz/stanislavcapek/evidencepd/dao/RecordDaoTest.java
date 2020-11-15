package cz.stanislavcapek.evidencepd.dao;

import cz.stanislavcapek.evidencepd.record.Record;
import cz.stanislavcapek.evidencepd.record.RecordDao;
import cz.stanislavcapek.evidencepd.shiftplan.ShfitPlan;
import cz.stanislavcapek.evidencepd.shiftplan.XlsxDao;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static cz.stanislavcapek.evidencepd.model.WorkingTimeFund.TypeOfWeeklyWorkingTime.MULTISHIFT_CONTINUOUS;

class RecordDaoTest {


    private static ShfitPlan PLAN;

    @BeforeAll
    static void beforeAll() throws IOException {
        Dao<XSSFWorkbook> io = new XlsxDao();
        XSSFWorkbook workbook = io.load(Path.of("src/test/resources/test_4_straznici.xlsx"));
        PLAN = new ShfitPlan(workbook, MULTISHIFT_CONTINUOUS);
    }

    @Test
    void uloz() {
        final Record record = PLAN.getRecord(1, 2);
        final RecordDao dao = new RecordDao();
        final Path path = Paths.get("src/test/resources/evidenceUlozitTest.json");
        try {
            dao.save(path, List.of(record));
            assert true;
        } catch (IOException e) {
            assert false;
            e.printStackTrace();
        }
    }

    @Test
    void nacti() {
        final RecordDao dao = new RecordDao();
        final Path path = Paths.get("src/test/resources/evidenceNacistTest.json");

        try {
            final List<Record> recordList = dao.load(path);
            System.out.println("recordList = " + recordList);
            assert true;
        } catch (IOException e) {
            assert false;
            e.printStackTrace();
        }


    }
}