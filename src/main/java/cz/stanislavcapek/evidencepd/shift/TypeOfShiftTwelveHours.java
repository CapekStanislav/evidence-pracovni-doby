package cz.stanislavcapek.evidencepd.shift;

public enum TypeOfShiftTwelveHours {
    /**
     * Určuje že se jedná o denní směnu, přičemž směna je odpracovaná v rozmezí
     * od půlnoci do 23:59:59 stejného dne.
     */
    DAY(12, "denní"),

    /**
     * Určuje, že se jedná o noční směnu, přičemž směna je odpracovaná v rozmezí
     * dvou dnů, tedy přes půlnoc.
     */
    NIGHT(12, "noční"),

    /**
     * Určuje, že se jedná o dovolenou, přičemž se předpokládá, že dovolená je
     * započítána v jeden den a její délka se rovná denní službě.
     */
    HOLIDAY(12, "dovolená"),

    /**
     * Určuje, že se jedná o polovinu dovolené, přičemž se předpokládá, že dovolená
     * je započítána v jeden den a její délka se rovná polovině denní službě.
     */
    HALF_HOLIDAY(6, "1/2 dovolené"),

    /**
     * Určuje, že se jedná o zdravotní službu, příčemž délka se odvíjí od
     * nastavení zaměstnavatele.
     */
    SICK_DAY(12, "zdravotní volno"),

    /**
     * Určuje, že se jedná o pracovní neschopnost, přičemž se předpokládá, že
     * je započítána jako denní služba.
     */
    INABILITY(12, "neschopnost"),

    /**
     * Určuje, že se jedná o ošetřování, přičemž délka se odvíjí od
     * nastavení zaměstnavatele.
     */
    HOME_CARE(12, "ošetřování"),

    /**
     * Určuje, že se jedná o školení započítané do pracovní doby.
     */
    TRAINING(7.5, "školení"),

    /**
     * Určuje, že se jedná o den, kdy neproběhla směna a její délka je {@code 0}.
     */
    NONE(0, "žádná");

    private final double shiftLength;
    private final String name;

    TypeOfShiftTwelveHours(double shiftLength, String name) {
        this.shiftLength = shiftLength;
        this.name = name;
    }

    /**
     * Vrátí základní délku směny pro daný typ.
     *
     * @return délku jako desetinné číslo hodin
     */
    public double getShiftLength() {
        return shiftLength;
    }

    @Override
    public String toString() {
        return name;
    }
}
