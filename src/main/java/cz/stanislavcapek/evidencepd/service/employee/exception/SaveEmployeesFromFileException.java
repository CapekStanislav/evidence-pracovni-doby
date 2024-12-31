package cz.stanislavcapek.evidencepd.service.employee.exception;

import java.nio.file.Path;

public class SaveEmployeesFromFileException extends RuntimeException {
    public SaveEmployeesFromFileException(Path path, Throwable e) {
        super(String.format("Loading employees from path '%s' failed.", path.toString()), e);
    }
}
