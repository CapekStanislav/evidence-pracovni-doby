package cz.stanislavcapek.evidencepd.view.component.evidence;

import cz.stanislavcapek.evidencepd.pdf.EvidenceDocument;
import cz.stanislavcapek.evidencepd.pdf.EvidencePracovniDobyPdfTovarna;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.swing.SwingWorker;

/**
 * Instance třídy {@code DocumentCreatingTask}
 *
 * @author Stanislav Čapek
 */
public
class DocumentCreatingTask extends SwingWorker<PDDocument, Integer> {

    private final EvidenceDocument smeny;
    private final EvidenceDocument prescasy;

    public DocumentCreatingTask(EvidenceDocument smeny, EvidenceDocument prescasy) {
        this.smeny = smeny;
        this.prescasy = prescasy;
    }

    @Override
    protected PDDocument doInBackground() throws Exception {
        final PDDocument document = new PDDocument();

        final PDDocument smenyDoc = EvidencePracovniDobyPdfTovarna
                .createEvidenciPDDocument(smeny);
        document.addPage(smenyDoc.getPage(0));

        if (prescasy.getRowCount() > 0) {
            final PDDocument prescasyDoc = EvidencePracovniDobyPdfTovarna
                    .createEvidenciPDDocument(prescasy, "Evidence přesčasů");
            document.addPage(prescasyDoc.getPage(0));
        }
        return document;
    }

}
