package cz.stanislavcapek.evidencepd.service.shiftplan;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.domain.shiftplan.ShiftPlan;
import cz.stanislavcapek.evidencepd.service.shiftplan.exception.LoadShiftPlanFailed;
import cz.stanislavcapek.evidencepd.service.template.XlsxDao;
import cz.stanislavcapek.evidencepd.service.template.exception.SaveShiftPlanTemplateFailed;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.nio.file.Path;

public class ShiftPlanService {

    private final XlsxDao dao;
    private final ShiftPlanFactory factory;

    @Inject
    public ShiftPlanService(XlsxDao dao, ShiftPlanFactory factory) {
        this.dao = dao;
        this.factory = factory;
    }

    public void saveTemplate(Path path, XSSFWorkbook workbook) {
        try {
            dao.save(path, workbook);
        } catch (Exception e) {
            throw new SaveShiftPlanTemplateFailed(path, e);
        }
    }

    public ShiftPlan loadShiftPlan(Path path) {
        try {
            XSSFWorkbook workbook = dao.load(path);
            return factory.create(workbook, WorkingTimeFund.TypeOfWeeklyWorkingTime.MULTISHIFT_CONTINUOUS);
        } catch (Exception e) {
            throw new LoadShiftPlanFailed(path, e);
        }
    }
}
