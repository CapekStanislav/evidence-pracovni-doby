package cz.stanislavcapek.evidencepd.swingui.view.workattendance;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.service.pdf.WorkAttendanceDocument;
import cz.stanislavcapek.evidencepd.service.pdf.WorkingTimeRecordPdfFactory;

public class DocumentCreatingTaskFactory {

    private final WorkingTimeRecordPdfFactory pdfFactory;

    @Inject
    public DocumentCreatingTaskFactory(WorkingTimeRecordPdfFactory pdfFactory) {
        this.pdfFactory = pdfFactory;
    }

    DocumentCreatingTask create(WorkAttendanceDocument shifts, WorkAttendanceDocument overtimes) {
        return new DocumentCreatingTask(shifts, overtimes, pdfFactory);
    }
}
