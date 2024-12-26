package cz.stanislavcapek.evidencepd.view.component.workattendance;

import com.google.inject.Inject;
import cz.stanislavcapek.evidencepd.pdf.WorkAttendanceDocument;
import cz.stanislavcapek.evidencepd.pdf.WorkingTimeRecordPdfFactory;

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
