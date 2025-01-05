package cz.stanislavcapek.evidencepd.swingui.model;

import cz.stanislavcapek.evidencepd.domain.shiftplan.ShiftPlan;

@FunctionalInterface
public interface ShiftPlanLoadedListener {
    void loaded(ShiftPlan shiftPlan);
}
