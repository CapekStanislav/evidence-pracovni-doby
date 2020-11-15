package cz.stanislavcapek.evidencepd.shiftplan;

import cz.stanislavcapek.evidencepd.dao.Dao;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * An instance of class {@code XlsxDao}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class XlsxDao implements Dao<XSSFWorkbook> {

    @Override
    public void save(Path path, XSSFWorkbook object) throws IOException {
        try (FileOutputStream out = new FileOutputStream(path.toFile())) {
            object.write(out);
        } catch (IOException e) {
            throw new IOException("Nepodařilo se uložit soubor: " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public XSSFWorkbook load(Path path) throws IOException {
        XSSFWorkbook workbook = null;
        try {
            workbook = XSSFWorkbookFactory.createWorkbook(path.toFile(), true);
        } catch (Exception e) {
            throw new IOException("Nepodařilo se načíst soubor. Cesta k souboru " + path, e);
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
        return workbook;
    }
}
