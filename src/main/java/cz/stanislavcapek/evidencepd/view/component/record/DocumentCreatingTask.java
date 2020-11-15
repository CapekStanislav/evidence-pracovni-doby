package cz.stanislavcapek.evidencepd.view.component.record;

import cz.stanislavcapek.evidencepd.pdf.RecordDocument;
import cz.stanislavcapek.evidencepd.pdf.WorkingTimeRecordPdfFactory;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.swing.SwingWorker;

/**
 * Instance třídy {@code DocumentCreatingTask}
 *
 * @author Stanislav Čapek
 */
public
class DocumentCreatingTask extends SwingWorker<PDDocument, Integer> {

    private final RecordDocument shifts;
    private final RecordDocument overtimes;

    public DocumentCreatingTask(RecordDocument shifts, RecordDocument overtimes) {
        this.shifts = shifts;
        this.overtimes = overtimes;
    }

    @Override
    protected PDDocument doInBackground() throws Exception {
        final PDDocument document = new PDDocument();

        final PDDocument shiftDoc = WorkingTimeRecordPdfFactory
                .createRecordPDDocument(shifts);
        document.addPage(shiftDoc.getPage(0));

        if (overtimes.getRowCount() > 0) {
            final PDDocument overtimeDoc = WorkingTimeRecordPdfFactory
                    .createRecordPDDocument(overtimes, "Evidence přesčasů");
            document.addPage(overtimeDoc.getPage(0));
        }
        return document;
    }

}
