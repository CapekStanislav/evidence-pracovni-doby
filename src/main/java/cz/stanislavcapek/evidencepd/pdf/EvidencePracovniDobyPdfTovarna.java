package cz.stanislavcapek.evidencepd.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.TextCell;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Instance třídy {@code EvidencePracovniDobyPdfTovarna}
 *
 * @author Stanislav Čapek
 */
public class EvidencePracovniDobyPdfTovarna {
    private static final float PADDING = 50f;
    private static final PDRectangle A4 = PDRectangle.A4;
    private static final float PRINTABLE_AREA = A4.getWidth() - 2 * PADDING;
    private static final int FONT_SIZE = 10;

    private static LocalDate obdobi;
    private static PDFont normalFont;
    private static PDFont boldFont;

    public static PDDocument createEvidenciPDDocument(EvidenceDocument model) throws IOException {
        return createEvidenciPDDocument(model, "Evidence pracovní doby");
    }

    public static PDDocument createEvidenciPDDocument(
            EvidenceDocument model, String title) throws IOException {
        obdobi = model.getDate(0);
        final float denSize = 40;
        final float timeSize = 40;
        final float typSize = 76;
        final float numSize = 49;

        // nastavení dokumentu
        final PDDocument document = new PDDocument();
        PDPage page = new PDPage(A4);
        document.addPage(page);

        // načtení písma
        if (normalFont == null || boldFont == null) {
            final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

            try (final InputStream inputStream = classLoader.getResourceAsStream("fonts/calibri.ttf")) {
                normalFont = PDType0Font.load(document, inputStream, true);
            }

            try (InputStream inputStream = classLoader.getResourceAsStream("fonts/calibrib.ttf")) {
                boldFont = PDType0Font.load(document, inputStream, true);
            }
        }

        final Table.TableBuilder builder = Table.builder();

        // nastavení prvních 4 sloupců
        final float[] columns = new float[model.getColumnCount()];
        columns[0] = denSize;
        columns[1] = timeSize;
        columns[2] = timeSize;
        columns[3] = typSize;
        // nastavení zbylých sloupců
        for (int i = 4; i < model.getColumnCount(); i++) {
            columns[i] = numSize;
        }

        // nastavení tabulky
        builder.addColumnsOfWidth(columns)
                .fontSize(FONT_SIZE)
                .font(normalFont)
                .borderColor(Color.BLACK);

        // vytvoření záhlaví tabulky
        Row.RowBuilder rowBuilder;
        builder.addRow(createTableHeader(model));

        // řádky směn
        createTableContent(model, builder);

        // vytvoření sumarizace
        builder.addRow(createTableSum(model));

        // zopakování záhlaví tabulky
//        builder.addRow(createTableHeader(model));

        final List<Table> tablesToDraw = new ArrayList<>();
        tablesToDraw.add(createHeader(model, title));
        tablesToDraw.add(builder.build());
        tablesToDraw.add(createFooter(model, normalFont));
        tablesToDraw.add(createSigning(normalFont));

        float starty = page.getMediaBox().getHeight() - PADDING;

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            for (Table table : tablesToDraw) {
                TableDrawer.builder()
                        .page(page)
                        .contentStream(contentStream)
                        .table(table)
                        .startX(PADDING)
                        .startY(starty)
                        .endY(PADDING)
                        .build()
                        .draw();
                starty -= table.getHeight() + PADDING / 2;
            }
        }

        return document;
    }
    //== KONSTRUKTORY A TOVÁRNÍ METODY =============================================

    //== ABSTRAKTNÍ METODY =========================================================
    //== PŘÍSTUPOVÉ METODY VLASTNOSTÍ INSTANCÍ =====================================
    //== OSTATNÍ NESOUKROMÉ METODY INSTANCÍ ========================================

    //== SOUKROMÉ A POMOCNÉ METODY TŘÍDY ===========================================
    //== SOUKROMÉ A POMOCNÉ METODY INSTANCÍ ========================================
    private static Row createTableSum(EvidenceDocument model) {
        Row.RowBuilder rowBuilder = Row.builder();
        rowBuilder
                .font(boldFont)
                .fontSize(12)
                .backgroundColor(Color.LIGHT_GRAY);
        rowBuilder.add(TextCell.builder().text("Celkem").colSpan(4).borderWidth(1).build());

        for (int i = 4; i < model.getColumnCount(); i++) {
            double count = 0;
            for (int r = 0; r < model.getRowCount(); r++) {
                final Object value = model.getValueAt(r, i);
                try {
                    count += Double.parseDouble(value.toString());
                } catch (NumberFormatException ignore) {
                }
            }
            rowBuilder.add(TextCell.builder().text(String.valueOf(count)).borderWidth(1).build());
        }
        return rowBuilder.build();
    }

    private static void createTableContent(EvidenceDocument model, Table.TableBuilder builder) {
        Row.RowBuilder rowBuilder;
        for (int i = 0; i < model.getRowCount(); i++) {
            rowBuilder = Row.builder();

            final DayOfWeek day = obdobi.withDayOfMonth(i + 1).getDayOfWeek();
            final Color color = day == DayOfWeek.SATURDAY ||
                    day == DayOfWeek.SUNDAY ? Color.lightGray : Color.white;
            rowBuilder.backgroundColor(color);

            for (int n = 0; n < model.getColumnCount(); n++) {
                rowBuilder.add(
                        TextCell.builder()
                                .text(model.getValueAt(i, n).toString())
                                .borderWidth(1)
                                .build()
                );

            }
            builder.addRow(rowBuilder.build());
        }
    }

    private static Row createTableHeader(EvidenceDocument model) {
        Row.RowBuilder rowBuilder = Row.builder();
        for (int i = 0; i < model.getColumnCount(); i++) {
            rowBuilder.add(
                    TextCell.builder()
                            .text(model.getColumnName(i))
                            .borderWidth(1)
                            .backgroundColor(Color.LIGHT_GRAY)
                            .build()
            );
        }
        return rowBuilder.build();
    }

    private static Table createHeader(EvidenceDocument model, String title) {
        final Table.TableBuilder builder = Table.builder();
        final float cellSize = PRINTABLE_AREA;
        builder
                .font(normalFont)
                .fontSize(12)
                .addColumnsOfWidth(cellSize);

        Row.RowBuilder rowBuilder = Row.builder();
        rowBuilder.add(
                TextCell.builder()
                        .text(title)
                        .horizontalAlignment(HorizontalAlignment.CENTER)
                        .font(boldFont)
                        .fontSize(16)
                        .build()
        );
        builder.addRow(rowBuilder.build());


        rowBuilder = Row.builder();
        final String jmeno = String.format("Jmeno: %s", model.getName());
        rowBuilder.add(TextCell.builder().text(jmeno).build());
        builder.addRow(rowBuilder.build());

        rowBuilder = Row.builder();
        final String obdobi = String.format("Období: %s %d", model.getMonth(), model.getYear());
        rowBuilder.add(TextCell.builder().text(obdobi).build());
        builder.addRow(rowBuilder.build());

        rowBuilder = Row.builder();
        final String fond = String.format("Fond pracovní doby: %.2f", model.getWorkTimeFund());
        rowBuilder.add(TextCell.builder().text(fond).build());
        builder.addRow(rowBuilder.build());


        return builder.build();

    }

    private static Table createFooter(EvidenceDocument model, PDFont normal) {
        final float cellSize = PRINTABLE_AREA / 3;
        final Table.TableBuilder builder = Table.builder();
        builder
                .font(normal)
                .fontSize(12)
                .addColumnsOfWidth(cellSize, cellSize, cellSize);

        final double fondPracovniDoby = model.getWorkTimeFund();
        final double zMinulehoMesice = model.getLastMonthHours();
        final double dalsihoMesice = model.getNextMonthHours();

        final String fond = String.format("Fond pracovní doby: %.2f", fondPracovniDoby);
        final String minuly = String.format("převod z minulého měsíce: %.2f", zMinulehoMesice);
        final String dalsi = String.format("převod do dalšího měsíce: %.2f", dalsihoMesice);

        builder.addRow(Row.builder()
                .add(TextCell.builder().text(fond).build())
                .add(TextCell.builder().text(minuly).build())
                .add(TextCell.builder().text(dalsi).build())
                .build()
        );

        return builder.build();
    }

    private static Table createSigning(PDFont normal) {
        final float cellSize = A4.getWidth() / 4;

        final Table.TableBuilder builder = Table.builder();

        builder
                .fontSize(12)
                .font(normal)
                .addColumnsOfWidth(cellSize, cellSize, cellSize, cellSize);

        builder.addRow(Row.builder()
                .add(TextCell.builder().text("Vystavil:").colSpan(2).build())
                .add(TextCell.builder().text("Zaměstnanec:").colSpan(2).build())
                .build()
        );

        builder.addRow(Row.builder()
                .add(TextCell.builder().text("").colSpan(4).build())
                .build()
        );

        builder.addRow(Row.builder()
                .add(TextCell.builder().text("Vydáno stravenek: ").colSpan(4).build())
                .build()
        );

        return builder.build();
    }
    //== INTERNÍ DATOVÉ TYPY =======================================================
    //== TESTY A METODA MAIN =======================================================

}
