package cz.stanislavcapek.evidencepd.plansmen;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Pomocná třída zodpovědná za formátování souboru XLSX.
 *
 * @author Stanislav Čapek
 */
public class XlsxFormatovani {
    private final XSSFWorkbook workbook;

    public XlsxFormatovani(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public XSSFCellStyle getStyleNadpisu() {
        // styl nadpisu //
        XSSFCellStyle nadpis = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 15);
        font.setBold(true);
        nadpis.setFont(font);
        nadpis.setAlignment(HorizontalAlignment.CENTER);
        return nadpis;
    }

    /**
     * Vytvoří styl hlavičky. Ohraničení H,S,L,P, zalamování textu, umístění textu na střed
     *
     * @return styl hlavičky tabulky
     */
    public XSSFCellStyle getStyleHlavicky() {
        // styl hlavička //
        XSSFCellStyle hlavicka = workbook.createCellStyle();
        XSSFFont fontHlavicka = workbook.createFont();
        fontHlavicka.setFontHeightInPoints((short) 10);
        fontHlavicka.setBold(true);
        hlavicka.setFont(fontHlavicka);
        hlavicka.setBorderTop(BorderStyle.THICK);
        hlavicka.setBorderBottom(BorderStyle.THICK);
        hlavicka.setBorderLeft(BorderStyle.THIN);
        hlavicka.setBorderRight(BorderStyle.THIN);
        hlavicka.setWrapText(true);
        hlavicka.setAlignment(HorizontalAlignment.CENTER);
        hlavicka.setVerticalAlignment(VerticalAlignment.CENTER);
        return hlavicka;
    }

    /**
     * Vytvoří styl zarovnání textu na střed.
     *
     * @return zarovnání textu na střed
     */
    public XSSFCellStyle getStyleCenter() {
        XSSFCellStyle center = workbook.createCellStyle();
        center.setAlignment(HorizontalAlignment.CENTER);
        center.setVerticalAlignment(VerticalAlignment.CENTER);
        return center;
    }

    /**
     * Vytvoří styl obarvené buňky s textem na středu
     *
     * @return barevné buňky s textem na středu
     */
    public XSSFCellStyle getStyleCenterColor() {
        XSSFCellStyle barva = workbook.createCellStyle();
//        barva.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
        barva.setFillPattern(FillPatternType.LESS_DOTS);
        barva.setBorderTop(BorderStyle.THIN);
        barva.setBorderBottom(BorderStyle.THIN);
        barva.setBorderLeft(BorderStyle.THIN);
        barva.setBorderRight(BorderStyle.THIN);
        barva.setAlignment(HorizontalAlignment.CENTER);
        barva.setVerticalAlignment(VerticalAlignment.BOTTOM);

        return barva;
    }

    /**
     * Vytvoří styl ohraničené buňky bez barvy, zarovnání textu na střed
     *
     * @return bezbarvá buňka s ohraničením, zarovnání textu na střed
     */
    public XSSFCellStyle getStyleCenterBorderNoColor() {
        XSSFCellStyle centerNoColor = workbook.createCellStyle();
        centerNoColor.setBorderTop(BorderStyle.THIN);
        centerNoColor.setBorderBottom(BorderStyle.THIN);
        centerNoColor.setBorderLeft(BorderStyle.THIN);
        centerNoColor.setBorderRight(BorderStyle.THIN);
        centerNoColor.setAlignment(HorizontalAlignment.CENTER);
        centerNoColor.setVerticalAlignment(VerticalAlignment.BOTTOM);

        return centerNoColor;
    }

    /**
     * Vytvoří styl bezbarvé buňky s ohraničením, zarovnání doleva.
     *
     * @return bezbarvá buňka s ohraničením, zarovnání textu doleva
     */
    public XSSFCellStyle getStyleBorderNoColor() {
        XSSFCellStyle boredrNoColor = workbook.createCellStyle();
        boredrNoColor.setBorderTop(BorderStyle.THIN);
        boredrNoColor.setBorderBottom(BorderStyle.THIN);
        boredrNoColor.setBorderLeft(BorderStyle.THIN);
        boredrNoColor.setBorderRight(BorderStyle.THIN);
        return boredrNoColor;
    }

    /**
     * Vytvoří styl bezbarvé buňky s dolním ohraničením.
     *
     * @return bezbarvá buňka s dolním ohraničením
     */
    public XSSFCellStyle getStyleBorderBottom() {
        XSSFCellStyle borderBottom = workbook.createCellStyle();
        borderBottom.setBorderBottom(BorderStyle.THIN);
        return borderBottom;
    }
}
