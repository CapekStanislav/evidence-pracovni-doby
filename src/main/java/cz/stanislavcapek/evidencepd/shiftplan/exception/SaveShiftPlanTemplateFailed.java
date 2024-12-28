package cz.stanislavcapek.evidencepd.shiftplan.exception;

import java.nio.file.Path;

public class SaveShiftPlanTemplateFailed extends RuntimeException {
    public SaveShiftPlanTemplateFailed(Path path, Throwable e) {
        super(String.format("Unable to save shift plan to '%s' path.", path.toString()), e);
    }
}
