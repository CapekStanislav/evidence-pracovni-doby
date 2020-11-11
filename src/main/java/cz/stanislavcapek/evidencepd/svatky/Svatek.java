package cz.stanislavcapek.evidencepd.svatky;

import java.time.LocalDate;

/**
 * Instance třídy {@code Svatek}
 *
 * @author Stanislav Čapek
 */
public class Svatek {
    // TODO: 20.02.2020 dodělat komentáře
    //== KONSTANTNÍ ATRIBUTY TŘÍDY =================================================
    //== PROMĚNNÉ ATRIBUTY TŘÍDY ===================================================
    //== STATICKÝ INICIALIZAČNÍ BLOK - STATICKÝ KONSTRUKTOR ========================
    //== KONSTANTNÍ ATRIBUTY INSTANCÍ ==============================================

    public final LocalDate datum;
    public final String nazev;

    //== PROMĚNNÉ ATRIBUTY INSTANCÍ ================================================
    //== PŘÍSTUPOVÉ METODY VLASTNOSTÍ TŘÍDY ========================================
    //== OSTATNÍ NESOUKROMÉ METODY TŘÍDY ===========================================

    //== KONSTRUKTORY A TOVÁRNÍ METODY =============================================
    public Svatek(LocalDate datum, String nazev) {
        this.datum = datum;
        this.nazev = nazev;
    }

    //== ABSTRAKTNÍ METODY =========================================================
    //== PŘÍSTUPOVÉ METODY VLASTNOSTÍ INSTANCÍ =====================================

    public LocalDate getDatum() {
        return datum;
    }

    public String getNazev() {
        return nazev;
    }

    //== OSTATNÍ NESOUKROMÉ METODY INSTANCÍ ========================================

    @Override
    public String toString() {
        return "Svatek{" +
                "datum=" + datum +
                ", nazev='" + nazev + '\'' +
                '}';
    }

    //== SOUKROMÉ A POMOCNÉ METODY TŘÍDY ===========================================
    //== SOUKROMÉ A POMOCNÉ METODY INSTANCÍ ========================================

    //== INTERNÍ DATOVÉ TYPY =======================================================
    //== TESTY A METODA MAIN =======================================================

}
