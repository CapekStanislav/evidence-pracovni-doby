package cz.stanislavcapek.evidencepd.plansmen;

import cz.stanislavcapek.evidencepd.evidence.Evidence;
import cz.stanislavcapek.evidencepd.model.Mesic;
import cz.stanislavcapek.evidencepd.smeny.Smena;
import cz.stanislavcapek.evidencepd.evidence.ZakladniEvidence;
import cz.stanislavcapek.evidencepd.zamestnanec.Zamestnanec;
import cz.stanislavcapek.evidencepd.model.FondPracovniDoby;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;

/**
 * An instance of class {@code PlanSmen}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@EqualsAndHashCode
@ToString
public class PlanSmen {

    @ToString.Exclude
    private final XSSFWorkbook workbook;
    private final FondPracovniDoby.DruhTydenniPracDoby tydenniPracDoba;
    @ToString.Exclude
    private final Map<Integer, Map<Integer, Evidence>> smenyZaRok = new TreeMap<>();
    private int rok;
    private Set<Integer> idZamestnancu;

    /**
     * Map {@code <Měsíc, Map<ID,Evidence>> }
     */

    public PlanSmen(XSSFWorkbook workbook, FondPracovniDoby.DruhTydenniPracDoby tydenniPracDoba) {
        this.workbook = workbook;
        this.tydenniPracDoba = tydenniPracDoba;
        this.rok = getRok();
        nactiCelyPlan();
    }

    /**
     * Metoda vrátí číslo roku v šabloně.
     *
     * @return číslo roku v šabloně, {@code nenalezeno} = 0
     */
    public int getRok() {
        XSSFCell rokCell = workbook.getSheetAt(0).getRow(0).getCell(0);
        if (rokCell != null) {
            if (rokCell.getCellType() == NUMERIC) {
                return (int) rokCell.getNumericCellValue();
            }
        }
        return 0;
    }

    /**
     * @param mesic měsíc
     * @param id    id zaměstnance
     * @return evidence směn
     * @throws IllegalArgumentException pokud hodnota měsíce není v rozmezí 1-12
     * @throws IllegalArgumentException pokud se id zaměstnance nenachází v daném měsíci
     */
    public Evidence getEvidenci(int mesic, int id) {
        if (!validaceMesice(mesic)) {
            throw new IllegalArgumentException("Neplatné číslo měsíce. Platné rozmezí je 1-12: " + mesic);
        }
        if (!isZamestnanec(id, mesic)) {
            final String s = String.format("Zaměstnanec s id %d se nenachází v zadaném měsíci.", id);
            throw new IllegalArgumentException(s);
        }
        return smenyZaRok.get(mesic).get(id);
    }

    /**
     * @param mesic měsíc
     * @return Mapu {@code <ID, Evidence>}
     * @throws IllegalArgumentException pokud hodnota měsíce není v rozmezí 1-12
     */
    public Map<Integer, Evidence> getEvidenceZaMesic(int mesic) {
        if (!validaceMesice(mesic)) {
            throw new IllegalArgumentException("Neplatné číslo měsíce. Platné rozmezí je 1-12: " + mesic);
        }
        return smenyZaRok.get(mesic);
    }

    /**
     * @param mesic měsíc
     * @param id    zaměstnance
     * @return seznam přesčasů
     */
    public Evidence getEvidenciPrescasu(int mesic, int id) {
        if (!validaceMesice(mesic)) {
            throw new IllegalArgumentException("Neplatné číslo měsíce. Platné rozmezí je 1-12: " + mesic);
        }
        if (!isZamestnanec(id, mesic)) {
            final String s = String.format("Zaměstnanec s id %d se nenachází v zadaném měsíci.", id);
            throw new IllegalArgumentException(s);
        }

        List<Smena> prescasyZaMesic;
        try {
            prescasyZaMesic = new PrescasyZaMesic(workbook, mesic, id).getPrescasy();
        } catch (Exception e) {
            prescasyZaMesic = new ArrayList<>();
        }
        return prevedNaEvidenci(prescasyZaMesic, getRok(), mesic, id);
    }

    /**
     * Zjistí, jestli se zaměstnanec nachází v celém plánu
     * směn.
     *
     * @param id označení zaměstnance
     * @return {@code true} zaměstnanec se nachází v plánu
     * {@code false} zaměstnanec se nenachází v plánu
     */
    public boolean isZamestnanec(int id) {
        return idZamestnancu.contains(id);
    }

    /**
     * Zjistí, jestli se zaměstnanec nachází v konkrétním měsíci.
     *
     * @param id    označení zaměstnance
     * @param mesic měsíc ve kterém hledáme (1-12)
     * @return {@code true} zaměstnanec se nachází v měsíci
     * {@code false} zaměstnanec se nenachází v měsíci
     * @throws IllegalArgumentException měsíc je mimo požadovaný rozsah
     */
    public boolean isZamestnanec(int id, int mesic) throws IllegalArgumentException {
        if (!validaceMesice(mesic)) {
            throw new IllegalArgumentException("Neplatné číslo měsíce. Platné rozmezí je 1-12: " + mesic);
        }
        return smenyZaRok.get(mesic).containsKey(id);
    }

    /**
     * Pomocná metoda pro vytvoření zaměstnance dle jeho ID
     *
     * @param id zaměstnance
     * @return nová instance zaměstnance
     */
    public Zamestnanec getZamestnance(int id) {
        final String jmeno = getJmenoZamestnance(id);
        final String[] split = jmeno.split(" ");

        return new Zamestnanec(
                id,
                split[0],
                split[1]
        );
    }

    public Set<Integer> getIdZamestnancu() {
        return idZamestnancu;
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

    /**
     * Vytvoří za jednotlivé měsíce v roce směny pro jednotlivé zaměstnance.
     * 1 měsíc -> (id, směny) * počet strážníků
     */
    private void nactiCelyPlan() {
        idZamestnancu = new TreeSet<>();
        for (int i = 1; i <= 12; i++) {
            int[] ids = getIdZamestnancePodleMesice(i);
            Map<Integer, Evidence> zaMesic = new TreeMap<>();
            final int pocetStrazniku = getPocetZamestnancu(i);
            for (int j = 0; j < pocetStrazniku; j++) {
                int id = ids[j];
                idZamestnancu.add(id);
                SmenyZaMesic smenyZaMesic = new SmenyZaMesic(
                        getCelyRadekPodleZamestnance(id, i),
                        i,
                        getZamestnance(id),
                        this.rok
                );
                Evidence evidence = prevedNaEvidenci(smenyZaMesic);
                zaMesic.put(id, evidence);
            }
            smenyZaRok.put(i, zaMesic);
        }
    }

    private Evidence prevedNaEvidenci(SmenyZaMesic smenyZaMesic) {
        Zamestnanec zamestnanec = smenyZaMesic.getZamestnanec();
        final Mesic mesic = Mesic.valueOf(smenyZaMesic.getMesic());
        Map<Integer, Smena> smeny = new TreeMap<>();

        smenyZaMesic.getSmeny()
                .forEach(smena -> smeny.put(smena.getZacatek().getDayOfMonth(), smena));
        return new ZakladniEvidence(
                zamestnanec,
                mesic,
                smenyZaMesic.getRok(),
                tydenniPracDoba,
                smenyZaMesic.getPrevodZMinulehoMesice(),
                smeny
        );
    }

    private Evidence prevedNaEvidenci(List<Smena> prescasy, int rok, int mesic, int id) {
        final Mesic mesicTyp = Mesic.valueOf(mesic);
        final Zamestnanec zamestnance = getZamestnance(id);

        final Map<Integer, Smena> smenaMap = prescasy.stream()
                .collect(Collectors.toMap(prescasy::indexOf, smena -> smena));

        return new ZakladniEvidence(
                zamestnance,
                mesicTyp,
                rok,
                tydenniPracDoba,
                0,
                smenaMap
        );
    }

    /**
     * Získá ID u zaměstnanců vygenerovaných v šabloně v zadaném měsíci.
     *
     * @param mesic měsíc ve kterém hledáme
     * @return seznam ID vygenerovaných strážníků
     */
    private int[] getIdZamestnancePodleMesice(int mesic) {
        final int pocetStrazniku = getPocetZamestnancu(mesic);
        final int[] pocet = new int[pocetStrazniku];
        mesic = mesic - 1;

        XSSFSheet sheet = workbook.getSheetAt(mesic);
        for (int i = 0; i < pocetStrazniku; i++) {
            XSSFRow row = sheet.getRow(2 + i);
            XSSFCell cell = row.getCell(2);
            pocet[i] = (int) cell.getNumericCellValue();
        }
        return pocet;
    }

    /**
     * Pomocná metoda, která vrací počet vygenerovaných zaměstnanců v šabloně
     * v daný měsíc.
     *
     * @param mesic 1-12
     * @return počet zaměstnanců
     */
    private int getPocetZamestnancu(int mesic) {
        mesic = mesic - 1;

        XSSFSheet sheet = workbook.getSheetAt(mesic);

        int pocet = 0;
        int rowIndex = 2;
        final XSSFRow sheetRow = sheet.getRow(rowIndex);
        Cell cell = null;
        if (sheetRow != null) {
            cell = sheetRow.getCell(2);
        }

        while (cell != null && cell.getNumericCellValue() > 0) {
            pocet++;
            rowIndex++;
            final XSSFRow row = sheet.getRow(rowIndex);
            if (row != null) {
                cell = row.getCell(2);
            } else {
                break;
            }
        }
        return pocet;
    }

    /**
     * Vrátí celý řádek, dle zadaného ID zaměstnance a měsíce (1-12).
     *
     * @param id    číslo zaměstnance
     * @param mesic měsíc (list) ve kterém hledám (1-12)
     * @return pole s textovou reprezentací celého řádku
     * @throws IllegalArgumentException měsíc je mimo rozmezí
     */
    private List<String> getCelyRadekPodleZamestnance(int id, int mesic) throws IllegalArgumentException {
        List<String> celyRadek;

        if (!validaceMesice(mesic)) {
            throw new IllegalArgumentException("Neplatné číslo měsíce. Platné rozmezí je 1-12: " + mesic);
        }

        int radekStraznika = getRadekZamestnance(id);

        XSSFRow row = workbook.getSheetAt(mesic - 1).getRow(radekStraznika);
        DataFormatter formatter = new DataFormatter();

        List<String> finalCelyRadek = new ArrayList<>();
        row.forEach(cell -> {
            if (cell.getCellType() == FORMULA) {
                if (cell.getCachedFormulaResultType() == NUMERIC) {
                    finalCelyRadek.add(cell.getNumericCellValue() + "");
                }
            } else {
                String obsah = formatter.formatCellValue(cell);
                finalCelyRadek.add(obsah);
            }
        });
        celyRadek = finalCelyRadek;

        return celyRadek;
    }

    /**
     * Vrátí číslo řádku (0 based) ve kterém se nachází zaměstnanec, vyhledaný dle ID.
     *
     * @param id číslo zaměstnance, kterého hledám
     * @return celé číslo řádku, nenalezeno = 0
     */
    private int getRadekZamestnance(int id) {
        final int[] i = {0};
        final int[] radekStraznika = {0};

        XSSFSheet sheet = workbook.getSheetAt(0);
        sheet.forEach(row -> {
            XSSFCell cell = (XSSFCell) row.getCell(2);
            if (cell != null) {
                if (cell.getCellType() == NUMERIC) {
                    if ((int) cell.getNumericCellValue() == id) {
                        radekStraznika[0] = i[0];
                    }
                }
            }
            i[0]++;
        });
        return radekStraznika[0];
    }

    /**
     * Získá jméno zaměstnance vygenerovaného v šabloně
     *
     * @param id číslo zaměstnance
     * @return String jméno zaměstnance,  prázdný String, pokud není nalezen strážník s odpovídajícím služebním
     * číslem
     */
    private String getJmenoZamestnance(int id) {
        String jmeno = "";
        int radek = getRadekZamestnance(id);

        if (radek != 0) {
            XSSFCell cell = workbook.getSheetAt(0).getRow(radek).getCell(0);
            if (cell != null) {
                jmeno = cell.getStringCellValue();
            }

        }
        return jmeno;
    }


}
