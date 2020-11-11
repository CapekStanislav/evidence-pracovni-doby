package cz.stanislavcapek.evidencepd.plansmen;

import cz.stanislavcapek.evidencepd.smeny.Smena;
import cz.stanislavcapek.evidencepd.smeny.TypSmeny;
import cz.stanislavcapek.evidencepd.zamestnanec.Zamestnanec;
import cz.stanislavcapek.evidencepd.smeny.TovarnaNaSmeny;
import cz.stanislavcapek.evidencepd.smeny.ZakladniTovarnaNaSmeny;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Třída představuje reprezentaci směn konkrétního zaměstnance v předem stanoveném měsíci. Předpokládá se, že před
 * vytvořením instance této třídy bude zkontrolován, zda šablona obsahuje daného zaměstnance.
 *
 * @author Stanislav Čapek
 */
class SmenyZaMesic {

    private final int mesic;
    private final LocalDate obdobi;
    private final int pocetDnuVMesici;
    private final List<String> celyRadek;
    private final int fondCell;
    private final int planCell;
    private final int prevodCell;
    private final Zamestnanec zamestnanec;
    private final int rok;
    private List<Smena> smeny;

    /**
     * Konstruktor
     *
     * @param celyRadekPodleStraznika řádek strážníka
     * @param mesic                   měsíc
     * @param zamestnanec             zaměstnanec
     * @param rok                     rok
     */
    SmenyZaMesic(List<String> celyRadekPodleStraznika, int mesic, Zamestnanec zamestnanec, int rok) {
        this.celyRadek = celyRadekPodleStraznika;
        this.mesic = mesic;
        this.zamestnanec = zamestnanec;
        this.rok = rok;
        this.obdobi = LocalDate.of(rok, this.mesic, 1);

        int size = celyRadekPodleStraznika.size();
        for (int i = celyRadekPodleStraznika.size() - 1; i >= 0; i--) {
            if (celyRadekPodleStraznika.get(i).equalsIgnoreCase("")) {
                size--;
            } else {
                break;
            }
        }

        this.fondCell = size - 5;
        this.planCell = fondCell + 1;
        int rozdilCell = planCell + 1;
        this.prevodCell = rozdilCell + 1;
        pocetDnuVMesici = obdobi.getMonth().length(obdobi.isLeapYear());
        getSeznamSluzeb();

    }

    /**
     * Kopírovací konstruktor
     *
     * @param toCopy instance této třídy ke kopírování
     */
    SmenyZaMesic(SmenyZaMesic toCopy) {
        this.mesic = toCopy.mesic;
        this.zamestnanec = toCopy.zamestnanec;
        this.rok = toCopy.rok;
        final LocalDate tempObdobi = toCopy.obdobi;
        this.obdobi = LocalDate.of(tempObdobi.getYear(), tempObdobi.getMonth(), tempObdobi.getDayOfMonth());
        this.pocetDnuVMesici = toCopy.pocetDnuVMesici;
        this.celyRadek = new ArrayList<>(toCopy.celyRadek);
        this.fondCell = toCopy.fondCell;
        this.planCell = toCopy.planCell;
        this.prevodCell = toCopy.prevodCell;
        this.smeny = new ArrayList<>(toCopy.smeny);
    }

    public Zamestnanec getZamestnanec() {
        return zamestnanec;
    }

    public List<Smena> getSmeny() {
        return this.smeny;
    }

    /**
     * Metoda vrátí hodnotu odpovídající fondu pracovní doby, tedy co musel zaměstnanec odpracovat, na daný měsíc.
     * Předpokládá se, že šablona plánu služeb obsahuje daného zaměstnance a tedy veškeré potřebné údaje.
     *
     * @return hodnotu představující fond pracovní doby
     */
    public double getFondPracovniDoby() {
        String temp = celyRadek.get(fondCell);
        return stringToDouble(temp);
    }

    /**
     * Vrátí seznam {@link Smena} odpracovaných v daném měsíci. Seznam je číslovaný od 0. Jeho velikost je
     * rovna počtu dnům v měsíci, pro který je seznam tvořen.
     *
     * @return seznam odpracovaných služeb
     */
    List<Smena> getSeznamSluzeb() {
        if (smeny == null) {
            vytvorSeznamSmen();
        }
        return smeny;
    }

    /**
     * Metoda vrací hodnotu přecházejících hodin z minulého měsíce do aktuálního měsíce.
     *
     * @return převod z minulého měsíce
     */
    public double getPrevodZMinulehoMesice() {
        String temp = celyRadek.get(prevodCell);
        return stringToDouble(temp);
    }

    public int getMesic() {
        return this.mesic;
    }

    public int getRok() {
        return this.rok;
    }

    // privátní metody

    /**
     * Pomocná metoda, která vytvoří a naplní seznam směn dle druhu,
     * případně dle zadaných hodin.
     */
    private void vytvorSeznamSmen() {

        smeny = new ArrayList<>();
//                nastavení továrny na odpovídající období
        LocalDate zacatek = LocalDate.of(this.rok, this.mesic, 1);
        TovarnaNaSmeny tovarna = new ZakladniTovarnaNaSmeny();
        tovarna.nastavObdobi(zacatek);

        for (int i = 3; i < pocetDnuVMesici + 3; i++) {
            String druh = celyRadek.get(i);
            if (druh != null) {
                TypSmeny typ = ziskejTypSmeny(druh);
                if (typ != null) {
                    smeny.add(tovarna.vytvorSmenu(zacatek, typ));
                } else {
                    try {
                        double delka = Double.parseDouble(druh);
                        smeny.add(tovarna.vytvorSmenu(
                                zacatek,
                                delka));
                    } catch (NumberFormatException e) {
                        smeny.add(tovarna.vytvorSmenu(zacatek, TypSmeny.ZADNA));
                    }
                }
            }
            zacatek = zacatek.plusDays(1);
        }
    }

    /**
     * Validuje zadaný den, dle měsíce.
     *
     * @param den zadaný den
     * @throws IllegalArgumentException pokud je den mimo měsíc
     */
    private void validaceDne(int den) throws IllegalArgumentException {
        if (den > smeny.size()) {
            throw new IllegalArgumentException("Zadaný den [" + den + "] se nenachází v tomto měsíci.");
        }
    }

    /**
     * Pomocná metoda namapuje zkratky ze šablony na {@link TypSmeny}.
     *
     * @param druh textová reprezentace druhu směny z plánu služeb
     * @return vrací {@code null} pokud neodpovídá výčtu
     */
    private TypSmeny ziskejTypSmeny(String druh) {
        TypSmeny typ;
        switch (druh.toLowerCase()) {
            case "d":
                typ = TypSmeny.DENNI;
                break;
            case "n":
                typ = TypSmeny.NOCNI;
                break;
            case "řd":
                typ = TypSmeny.DOVOLENA;
                break;
            case "pd":
                typ = TypSmeny.PULDENNI_DOVOLENA;
                break;
            case "zv":
                typ = TypSmeny.ZDRAV_VOLNO;
                break;
            case "pn":
                typ = TypSmeny.NESCHOPNOST;
                break;
            default:
                typ = null;
        }
        return typ;
    }

    /**
     * Převede text představující číslo např. 172,5 na hodnotu dat. typu double 172.5
     *
     * @param cislo textová reprezentace desetinného čísla
     * @return převedený text na double
     */
    private double stringToDouble(String cislo) throws NumberFormatException {
        double fondPracDoby;
        if (cislo.contains(",")) {
            cislo = cislo.replace(",", ".");
        }
        try {
            fondPracDoby = Double.parseDouble(cislo);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Předaný string nelze převést na desetinné číslo");
        }
        return fondPracDoby;
    }
}
