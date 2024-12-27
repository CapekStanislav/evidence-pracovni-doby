package cz.stanislavcapek.evidencepd.ui.component.workattendance;

import cz.stanislavcapek.evidencepd.pdf.WorkAttendanceDocument;
import cz.stanislavcapek.evidencepd.pdf.WorkingTimeRecordPdfFactory;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.swing.*;

public class DocumentCreatingTask extends SwingWorker<PDDocument, Integer> {

    private final WorkAttendanceDocument shifts;
    private final WorkAttendanceDocument overtimes;
    private final WorkingTimeRecordPdfFactory pdfFactory;

    public DocumentCreatingTask(WorkAttendanceDocument shifts, WorkAttendanceDocument overtimes, WorkingTimeRecordPdfFactory pdfFactory) {
        this.shifts = shifts;
        this.overtimes = overtimes;
        this.pdfFactory = pdfFactory;
    }

    @Override
    protected PDDocument doInBackground() throws Exception {
        final PDDocument document = new PDDocument();

        final PDDocument shiftDoc = pdfFactory
                .createRecordPDDocument(shifts);
        document.addPage(shiftDoc.getPage(0));

        if (overtimes.getRowCount() > 0) {
            final PDDocument overtimeDoc = pdfFactory
                    .createRecordPDDocument(overtimes, "Evidence přesčasů");
            document.addPage(overtimeDoc.getPage(0));
        }

        return document;
    }

}
