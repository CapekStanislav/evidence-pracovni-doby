package cz.stanislavcapek.evidencepd.domain.shiftplan;

import java.util.Arrays;

public enum Month {

    JANUARY(1, "leden"),
    FEBRUARY(2, "únor"),
    MARCH(3, "březen"),
    APRIL(4, "duben"),
    MAY(5, "květen"),
    JUNE(6, "červen"),
    JULY(7, "červenec"),
    AUGUST(8, "srpen"),
    SEPTEMBER(9, "září"),
    OCTOBER(10, "říjen"),
    NOVEMBER(11, "listopad"),
    DECEMBER(12, "prosinec");

    private final int order;
    private final String monthName;

    Month(int order, String monthName) {
        this.order = order;
        this.monthName = monthName;
    }

    public int getOrder() {
        return order;
    }

    public String getMonthName() {
        return monthName;
    }

    public static Month getByName(String name) {
        return Arrays.stream(values())
                .filter(month -> month.getMonthName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow();

    }

    public static Month getByOrder(int order) {
        return Arrays.stream(values())
                .filter(month -> month.getOrder() == order)
                .findFirst()
                .orElseThrow();

    }
}