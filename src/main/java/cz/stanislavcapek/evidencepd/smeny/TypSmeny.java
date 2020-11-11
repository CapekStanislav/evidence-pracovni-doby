package cz.stanislavcapek.evidencepd.smeny;

public enum TypSmeny {
    /**
     * Určuje že se jedná o denní směnu, přičemž směna je odpracovaná v rozmezí
     * od půlnoci do 23:59:59 stejného dne.
     */
    DENNI(12, "denní"),

    /**
     * Určuje, že se jedná o noční směnu, přičemž směna je odpracovaná v rozmezí
     * dvou dnů, tedy přes půlnoc.
     */
    NOCNI(12, "noční"),

    /**
     * Určuje, že se jedná o dovolenou, přičemž se předpokládá, že dovolená je
     * započítána v jeden den a její délka se rovná denní službě.
     */
    DOVOLENA(12, "dovolená"),

    /**
     * Určuje, že se jedná o polovinu dovolené, přičemž se předpokládá, že dovolená
     * je započítána v jeden den a její délka se rovná polovině denní službě.
     */
    PULDENNI_DOVOLENA(6, "1/2 dovolené"),

    /**
     * Určuje, že se jedná o zdravotní službu, příčemž délka se odvíjí od
     * nastavení zaměstnavatele.
     */
    ZDRAV_VOLNO(12, "zdravotní volno"),

    /**
     * Určuje, že se jedná o pracovní neschopnost, přičemž se předpokládá, že
     * je započítána jako denní služba.
     */
    NESCHOPNOST(12, "neschopnost"),

    /**
     * Určuje, že se jedná o ošetřování, přičemž délka se odvíjí od
     * nastavení zaměstnavatele.
     */
    OSETROVANI(12, "ošetřování"),

    /**
     * Určuje, že se jedná o školení započítané do pracovní doby.
     */
    SKOLENI(7.5, "školení"),

    /**
     * Určuje, že se jedná o den, kdy neproběhla směna a její délka je {@code 0}.
     */
    ZADNA(0, "žádná");

    private final double delkaSmeny;
    private final String name;

    TypSmeny(double delkaSmeny, String name) {
        this.delkaSmeny = delkaSmeny;
        this.name = name;
    }

    /**
     * Vrátí základní délku směny pro daný typ.
     *
     * @return délku jako desetinné číslo hodin
     */
    public double getDelkaSmeny() {
        return delkaSmeny;
    }

    @Override
    public String toString() {
        return name;
    }
}
