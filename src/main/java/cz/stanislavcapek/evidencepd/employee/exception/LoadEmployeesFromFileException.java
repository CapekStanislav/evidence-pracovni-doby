package cz.stanislavcapek.evidencepd.employee.exception;

import java.nio.file.Path;

public class LoadEmployeesFromFileException extends RuntimeException {
    public LoadEmployeesFromFileException(Path path, Throwable e) {
        super(String.format("Loading employees from path '%s' failed.", path.toString()), e);
    }
}
