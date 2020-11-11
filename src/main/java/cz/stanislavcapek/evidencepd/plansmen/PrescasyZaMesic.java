package cz.stanislavcapek.evidencepd.plansmen;

import cz.stanislavcapek.evidencepd.smeny.Smena;
import cz.stanislavcapek.evidencepd.smeny.TypSmeny;
import cz.stanislavcapek.evidencepd.smeny.TovarnaNaSmeny;
import cz.stanislavcapek.evidencepd.smeny.ZakladniTovarnaNaSmeny;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Třída představuje reprezentaci přesčasových směn konkrétního zaměstnance ve stanoveném měsíci.
 * Ty jsou načteny ze šablony, resp. z {@link PlanSmen} z předem určené sekce.
 *
 * @author Stanislav Čapek
 */
class PrescasyZaMesic {

    List<Smena> prescasy = new ArrayList<>();

    private final XSSFSheet sheet;

    /**
     * Konstruktor
     *
     * @param workbook      excelový plan směn
     * @param mesic         číslo měsíce v rozmezí 1-12
     * @param sluzebniCislo číslo zaměstnance
     * @throws InvalidFormatXslxExeption v případě, kdy se nepodaří nalézt
     *                                   v šabloně sekci s přesčasy
     * @throws IllegalArgumentException  měsíc je mimo rozmezí
     */
    PrescasyZaMesic(XSSFWorkbook workbook, int mesic, int sluzebniCislo) {
        if (!validaceMesice(mesic)) {
            throw new IllegalArgumentException("Neplatné číslo měsíce. Platné rozmezí je 1-12: " + mesic);
        }

        mesic--;
        sheet = workbook.getSheetAt(mesic);
        String hlavickaPrescasy = sheet.getRow(15).getCell(0).getStringCellValue();
        if (!hlavickaPrescasy.equalsIgnoreCase("přesčasy")) {
            throw new InvalidFormatXslxExeption("Nepodařilo se nalézt v šabloně sekci s přesčasy.");
        }

        try {
            nactiPrescasy(sluzebniCislo);
        } catch (Exception e) {
            throw new InvalidFormatXslxExeption("Chyba při čtení záznamu přesčasů.", e);
        }
    }

    /**
     * Vrací kopii seznamu přesčasů.
     *
     * @return seznam přečasů
     */
    List<Smena> getPrescasy() {
        return this.prescasy;
    }

    /**
     * Metoda projde sekci přesčasů v plánu služeb a pro zadané číslo
     * zaměstnance vyhledá a vytvoří seznam přesčasů.
     *
     * @param id číslo zaměstnance
     */
    private void nactiPrescasy(int id) {
        int indexOfRow = 17;
        XSSFRow entryRow = sheet.getRow(indexOfRow);
        TovarnaNaSmeny tovarna = new ZakladniTovarnaNaSmeny();

        while (entryRow != null) {
            final XSSFCell entryDate = entryRow.getCell(1);
            final XSSFCell entryStart = entryRow.getCell(2);
            final XSSFCell entryEnd = entryRow.getCell(3);
            final XSSFCell entryId = entryRow.getCell(4);

            // kontrola prázdných hodnot - true přeskakuji řádek
            if (entryDate == null || entryStart == null || entryEnd == null || entryId == null) {
                entryRow = sheet.getRow(++indexOfRow);
                continue;
            }

            final int idCellValue = (int) entryId.getNumericCellValue();
            if (id != idCellValue) {
                entryRow = sheet.getRow(++indexOfRow);
                continue;
            }

            final Calendar calendar = DateUtil.getJavaCalendar(entryDate.getNumericCellValue());
            final LocalTime startTime = ziskejCasZTextu(entryStart);
            final LocalTime endTime = ziskejCasZTextu(entryEnd);

            final LocalDate datum = LocalDate.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
            );


            tovarna.nastavObdobi(datum);

            LocalDateTime zacatek = LocalDateTime.of(datum, startTime);
            LocalDateTime konec = LocalDateTime.of(datum, endTime);

            if (startTime.isAfter(endTime)) {
                konec = konec.plusDays(1);
            }

            final Smena smena = tovarna.vytvorSmenu(zacatek, konec, TypSmeny.DENNI);
            this.prescasy.add(smena);

            entryRow = sheet.getRow(++indexOfRow);
        }
    }

    /**
     * Z buňky obsahujicí čas vytvoří pole [hodiny,minuty].
     *
     * @param entryTime buňka obsahující čas
     * @return časový údaj, {@link LocalTime#MIN} pokud se nepodaři extrahovat
     * čas z buňky
     */
    private LocalTime ziskejCasZTextu(Cell entryTime) {
        switch (entryTime.getCellType()) {
            case STRING:
                final String[] split = entryTime.getStringCellValue().split(":");
                final int hour = Integer.parseInt(split[0]);
                final int minute = Integer.parseInt(split[1]);
                return LocalTime.of(hour, minute);

            case NUMERIC:
                final Date date = entryTime.getDateCellValue();
                return LocalTime.of(date.getHours(), date.getMinutes());

            default:
                return LocalTime.MIN;
        }
    }

    /**
     * Metoda validuje zadaný měsíc, který musí být v rozmezí 1 - 12
     *
     * @param mesic číslo měsíce
     * @return nachází se v požadovaném rozmezí
     */
    private boolean validaceMesice(int mesic) {
        return mesic > 0 && mesic < 13;
    }
}
