/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.stanislavcapek.evidencepd.plansmen;

import cz.stanislavcapek.evidencepd.model.Mesic;
import cz.stanislavcapek.evidencepd.zamestnanec.Zamestnanec;
import cz.stanislavcapek.evidencepd.model.FondPracovniDoby;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Třída slouží k vytvoření šablony XLSX v přesně daném formátu (plán služeb).
 * Vytvoří jméno pracovního sešitu, s uvedeným rokem a hlavičkou tabulky. Další obsah se odvíjí od použité přetížené
 * metody {@link VytvoreniSablonyXlsx#vytvor()}.
 *
 * @author Stanislav Čapek
 */

public class VytvoreniSablonyXlsx {
    private static final List<Zamestnanec> EMPTY_LIST = new ArrayList<>();

    /**
     * Vytvoří excelovou šablonu pro plánování služeb. Rozdělena na jednotlivé měsíce v roce. Automaticky generuje
     * dny dle <b>aktuálního roku</b> pro jednotlivé měsíce. Barevně rozliší soboty a neděle.<br>
     * Obsahuje pouze první prázdný řádek, bez vyplnění konkrétního zaměstnance.
     *
     * @return {@link XSSFWorkbook} šablona plánu služeb
     */
    public static XSSFWorkbook vytvor() {
        if (EMPTY_LIST.size() == 0) {
            EMPTY_LIST.add(new Zamestnanec(0, "", ""));
        }
        return vytvor(EMPTY_LIST);
    }

    /**
     * Vytvoří excelovou šablonu pro plánování služeb. Rozdělena na jednotlivé měsíce v roce. Automaticky generuje
     * dny dle <b>aktuálního roku</b> pro jednotlivé měsíce. Barevně rozliší soboty a neděle.<br>
     * Vygeneruje řádky pro jednotlivé zaměstnance, předané v parametru.
     *
     * @param zamestnanecList zaměstnanci ke generování
     * @return {@link XSSFWorkbook} šablona plánu služeb
     */
    public static XSSFWorkbook vytvor(List<Zamestnanec> zamestnanecList) {
        return vytvor(zamestnanecList, LocalDate.now().getYear());
    }

    /**
     * Vytvoří excelovou šablonu pro plánování služeb. Rozdělena na jednotlivé měsíce v roce. Automaticky generuje
     * dny dle <b>zadaného roku</b> pro jednotlivé měsíce. Barevně rozliší soboty a neděle.<br>
     * Vygeneruje řádky pro jednotlivé zaměstnance, předané v parametru.
     *
     * @param zamestnanecList zaměstnanci ke generování
     * @param rok             celé číslo roku
     * @return {@link XSSFWorkbook} šablona plánu služeb
     */
    public static XSSFWorkbook vytvor(List<Zamestnanec> zamestnanecList, int rok) {

        List<Zamestnanec> zamList = zamestnanecList;
        LocalDate datum = LocalDate.of(rok, 1, 1);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XlsxFormatovani format = new XlsxFormatovani(workbook);
        int doDalsihoMesice = 0;
        final Mesic[] mesice = Mesic.values();
        for (int i = 0; i < mesice.length; i++) {
            String tempMesic = mesice[i].getNazev();

            XSSFSheet sheet = workbook.createSheet(tempMesic);

            // row 0 //
            XSSFRow row = sheet.createRow(0);
            XSSFCell cell;

            cell = row.createCell(0);
            cell.setCellValue(rok);
            cell.setCellStyle(format.getStyleCenter());

            cell = row.createCell(3);
            cell.setCellValue("Měsíční rozpis služeb - Městská policie Třebechovice pod Orebem");
            cell.setCellStyle(format.getStyleNadpisu());

            // row 1 //
            // info o strážnících //
            row = sheet.createRow(1);

            cell = row.createCell(0);
            cell.setCellValue(tempMesic);
            cell.setCellStyle(format.getStyleHlavicky());

            cell = row.createCell(1);
            cell.setCellValue("");
            cell.setCellStyle(format.getStyleHlavicky());

            cell = row.createCell(2);
            cell.setCellValue("Sl.č.");
            cell.setCellStyle(format.getStyleHlavicky());

            // dny v měsíci //
            for (int j = 3; j < datum.lengthOfMonth() + 3; j++) {
                cell = row.createCell(j);
                cell.setCellValue(j - 2);
                cell.setCellStyle(format.getStyleHlavicky());
            }

            // info o hodinách //
            short posledni = row.getLastCellNum();
            String[] info = {"", "má být", "plán", "rozdíl", "převod", "do dal. měsíce"};

            for (int j = 0; j < info.length; j++) {
                cell = row.createCell(posledni + j);
                if (j == info.length - 1) {
                    if (i == 5 || i == 11) {
                        cell.setCellValue("Vyrov. období");
                    } else {
                        cell.setCellValue(info[j]);
                    }
                } else {
                    cell.setCellValue(info[j]);
                }
                cell.setCellStyle(format.getStyleHlavicky());
            }

            // row 2 + j //
            for (int j = 0; j < zamList.size(); j++) {
                Zamestnanec zamestnanec = zamList.get(j);
                row = sheet.createRow(2 + j);

                cell = row.createCell(0);
                cell.setCellValue(zamestnanec.getCeleJmeno());
                cell.setCellStyle(format.getStyleBorderNoColor());

                cell = row.createCell(1);
                cell.setCellValue(zamestnanec.getZkratku());
                cell.setCellStyle(format.getStyleBorderNoColor());

                cell = row.createCell(2);
                cell.setCellValue(zamestnanec.getId());
                cell.setCellStyle(format.getStyleCenterBorderNoColor());

                LocalDate tempDatum = datum;
                for (int k = 3; k < datum.lengthOfMonth() + 3; k++) {
                    cell = row.createCell(k);
                    if (tempDatum.getDayOfWeek() == DayOfWeek.SATURDAY || tempDatum.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        cell.setCellStyle(format.getStyleCenterColor());
                    } else {
                        cell.setCellStyle(format.getStyleCenterBorderNoColor());
                    }
                    tempDatum = tempDatum.plusDays(1);

                }

                cell = row.createCell(posledni);
                cell.setCellValue(zamestnanec.getZkratku());
                cell.setCellStyle(format.getStyleCenterBorderNoColor());

                //má být
                cell = row.createCell(posledni + 1);
                cell.setCellStyle(format.getStyleCenterBorderNoColor());
                cell.setCellValue(FondPracovniDoby.vypoctiFondPracovniDoby(datum));

                // plán
                cell = row.createCell(posledni + 2);
                cell.setCellStyle(format.getStyleCenterBorderNoColor());

                int d = datum.lengthOfMonth();
                int r = 2 + j;
                CellReference ref1 = new CellReference(r, 3);
                CellReference ref2 = new CellReference(r, d + 2);
                String oblast = ref1.formatAsString() + ":" + ref2.formatAsString();
                cell.setCellFormula("(COUNTIF(" + oblast + ",\"d\")*12)+(COUNTIF(" + oblast + ",\"n\")*12)+(COUNTIF(" +
                        oblast + ",\"řd\")*12)+(COUNTIF(" + oblast + ",\"pd\")*12)+(COUNTIF(" +
                        oblast + ",\"zv\")*12)+(COUNTIF(" + oblast + ",\"pn\")*12)+SUM(" +
                        oblast + ")");

                // rozdíl
                cell = row.createCell(posledni + 3);
                cell.setCellStyle(format.getStyleCenterBorderNoColor());
                ref1 = new CellReference(ref2.getRow(), ref2.getCol() + 2);
                ref2 = new CellReference(ref1.getRow(), ref1.getCol() + 1);
                cell.setCellFormula(ref2.formatAsString() + "-" + ref1.formatAsString());

                // převod
                cell = row.createCell(posledni + 4);
                cell.setCellStyle(format.getStyleCenterBorderNoColor());
                if (i == 0) {
                    cell.setCellValue(0);
                } else {
                    CellReference minulyMesic = new CellReference(workbook.getSheetName(i - 1), r, doDalsihoMesice, false, false);
                    cell.setCellFormula(minulyMesic.formatAsString());
                }

                // převod do dalšího měsíce
                cell = row.createCell(posledni + 5);
                cell.setCellStyle(format.getStyleCenterBorderNoColor());
                ref1 = new CellReference(ref2.getRow(), ref2.getCol() + 1);
                ref2 = new CellReference(ref1.getRow(), ref1.getCol() + 1);
                cell.setCellFormula(ref1.formatAsString() + "+" + ref2.formatAsString());
            }
            doDalsihoMesice = cell.getColumnIndex();

            // legenda poslední řada + 2 //
            String[][] legenda = {{"Legenda", ""}, {"Denní", "d"}, {"Noční", "n"}, {"Řádná dovolená", "řd"},
                    {"Půlden dovolené", "pd"}, {"Zdravotní volno", "zv"}, {"Prac. neschopnost", "pn"}};
            int lastRow = sheet.getLastRowNum();
            for (int j = 0; j < legenda.length; j++) {
                row = sheet.createRow(lastRow + 2 + j);
                cell = row.createCell(0);
                cell.setCellValue(legenda[j][0]);
                if (j == 0) {
                    cell.setCellStyle(format.getStyleBorderBottom());
                }

                cell = row.createCell(1);
                cell.setCellValue(legenda[j][1]);

                if (j == 0) {
                    cell.setCellStyle(format.getStyleBorderBottom());
                }
            }

            // přesčasy poslední řada + 2 //
            String[][] prescasy = {{"Přesčasy", "", "", "", ""}, {"Důvod", "datum", "od hod.", "do hod.", "služ.číslo"}};
            lastRow = sheet.getLastRowNum();
            for (int j = 0; j < prescasy.length; j++) {
                row = sheet.createRow(lastRow + 2 + j);
                for (int k = 0; k < prescasy[j].length; k++) {
                    cell = row.createCell(k);
                    if (j == 0) {
                        cell.setCellStyle(format.getStyleBorderBottom());
                    }
                    cell.setCellValue(k);
                    cell.setCellValue(prescasy[j][k]);
                }
            }

            // nastevní šířky sloupce u jmen //
            for (int j = 0; j < 3; j++) {
                sheet.autoSizeColumn(j);
            }

            // nastavení šířky sloupce u dní v týdnu //
            for (int j = 3; j < datum.lengthOfMonth() + 3; j++) {
                sheet.setColumnWidth(j, 1250);
            }

            sheet.setColumnWidth(3, 1750);
            // formátování merge //
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, datum.lengthOfMonth()));

            // posune se na další měsíc
            datum = datum.plusMonths(1);
        }
        return workbook;
    }

}
