package cz.stanislavcapek.evidencepd.plansmen;

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
    public void uloz(Path cesta, XSSFWorkbook objekt) throws IOException {
        try (FileOutputStream out = new FileOutputStream(cesta.toFile())) {
            objekt.write(out);
        } catch (IOException e) {
            throw new IOException("Nepodařilo se uložit soubor: " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public XSSFWorkbook nacti(Path cesta) throws IOException {
        XSSFWorkbook workbook = null;
        try {
            workbook = XSSFWorkbookFactory.createWorkbook(cesta.toFile(), true);
        } catch (Exception e) {
            throw new IOException("Nepodařilo se načíst soubor. Cesta k souboru " + cesta, e);
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
        return workbook;
    }
}
