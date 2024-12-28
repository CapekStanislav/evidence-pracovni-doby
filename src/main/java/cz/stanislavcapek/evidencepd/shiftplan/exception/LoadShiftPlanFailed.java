package cz.stanislavcapek.evidencepd.shiftplan.exception;

import java.nio.file.Path;

public class LoadShiftPlanFailed extends RuntimeException {
    public LoadShiftPlanFailed(Path path, Throwable e) {
        super(String.format("Unable to load shift plan from '%s' path.", path.toString()), e);
    }
}
