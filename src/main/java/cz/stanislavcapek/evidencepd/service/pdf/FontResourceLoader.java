package cz.stanislavcapek.evidencepd.service.pdf;

import cz.stanislavcapek.evidencepd.service.pdf.exception.UnableToLoadFontException;

import java.io.InputStream;

public class FontResourceLoader {

    InputStream loadCalibriFont() {
        return tryLoadFont("calibri");
    }

    InputStream loadCalibribFont() {
        return tryLoadFont("calibrib");
    }


    private InputStream tryLoadFont(String fontName) {
        try {
            return getClass().getClassLoader().getResourceAsStream(
                    String.format("fonts/%s.ttf", fontName)
            );
        } catch (Exception e) {
            throw new UnableToLoadFontException(fontName, e);
        }
    }
}
