package cz.stanislavcapek.evidencepd.dao;

import cz.stanislavcapek.evidencepd.evidence.EvidenceDao;
import cz.stanislavcapek.evidencepd.evidence.Evidence;
import cz.stanislavcapek.evidencepd.plansmen.PlanSmen;
import cz.stanislavcapek.evidencepd.plansmen.XlsxDao;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static cz.stanislavcapek.evidencepd.model.FondPracovniDoby.DruhTydenniPracDoby.VICESMENNY_NEPRETRZITY;

class EvidenceDaoTest {


    private static PlanSmen PLAN;

    @BeforeAll
    static void beforeAll() throws IOException {
        Dao<XSSFWorkbook> io = new XlsxDao();
        XSSFWorkbook workbook = io.nacti(Path.of("src/test/resources/test_4_straznici.xlsx"));
        PLAN = new PlanSmen(workbook, VICESMENNY_NEPRETRZITY);
    }

    @Test
    void uloz() {
        final Evidence evidence = PLAN.getEvidenci(1, 2);
        final EvidenceDao dao = new EvidenceDao();
        final Path path = Paths.get("src/test/resources/evidenceUlozitTest.json");
        try {
            dao.uloz(path, List.of(evidence));
            assert true;
        } catch (IOException e) {
            assert false;
            e.printStackTrace();
        }
    }

    @Test
    void nacti() {
        final EvidenceDao dao = new EvidenceDao();
        final Path path = Paths.get("src/test/resources/evidenceNacistTest.json");

        try {
            final List<Evidence> evidenceList = dao.nacti(path);
            System.out.println("evidenceList = " + evidenceList);
            assert true;
        } catch (IOException e) {
            assert false;
            e.printStackTrace();
        }


    }
}