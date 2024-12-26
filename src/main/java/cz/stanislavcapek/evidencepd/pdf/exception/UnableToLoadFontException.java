package cz.stanislavcapek.evidencepd.pdf.exception;

public class UnableToLoadFontException extends RuntimeException {

    public UnableToLoadFontException(String fontName, Exception e) {
        super(String.format("Unable to load '%s' font.", fontName), e);
    }
}
