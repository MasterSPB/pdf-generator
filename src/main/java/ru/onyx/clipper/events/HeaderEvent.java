package ru.onyx.clipper.events;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.w3c.dom.NodeList;
import ru.onyx.clipper.data.PropertyGetter;
import ru.onyx.clipper.model.BaseReportObject;
import ru.onyx.clipper.model.Report;
import ru.onyx.clipper.model.ReportParagraph;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by Anton on 19.05.2014.
 */
public class HeaderEvent extends PdfPageEventHelper {
    /** The header text. */
    String header;
    String pageNumType;
    String pageText;
    String pageNumHPos;
    String pageNumVPos;

    Document _doc;
    static int curPage;

    Font headerFont;

    private ArrayList<BaseReportObject> headerItems = new ArrayList<BaseReportObject>();

    public HeaderEvent(Report rep, Document _document, Font headerFont) {
        _doc = _document;
        curPage = rep.getCurPage();
        pageNumHPos = rep.getRepPageNumHPos();
        pageNumVPos = rep.getRepPageNumVPos();
        this.headerFont = headerFont;
        pageNumType = rep.getPageNumType();
        pageText = rep.getPageText();
        headerItems = rep.getHeaderItems();
    }

    /** The template with the total number of pages. */
    PdfTemplate total;

    /**
     * Allows us to change the content of the header.
     * @param header The new header String
     */
    public void setHeader(String header) {

        this.header = header;
    }

    /**
     * Creates the PdfTemplate that will hold the total number of pages.
     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(
     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
     */
    public void onOpenDocument(PdfWriter writer, Document document) {
        total = writer.getDirectContent().createTemplate(30, 16);
    }



    protected void drawHeader(PdfWriter writer, ArrayList<BaseReportObject> headerItems){
        // draws header inside margins
        ColumnText ct = new ColumnText(writer.getDirectContent());
        ct.setSimpleColumn(_doc.leftMargin(),_doc.topMargin(), _doc.getPageSize().getWidth() - _doc.rightMargin(),_doc.getPageSize().getHeight() * 0.99f);

        for (BaseReportObject headerItem : headerItems){
            try {
                ct.addElement(headerItem.getPdfObject());
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            ct.go();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    /**
     * Adds a page header to every page
     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(
     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
     */
    public void onEndPage(PdfWriter writer, Document document) {
        int startPage=0;
        PdfPTable table = new PdfPTable(8); // a table for page part of the header

        if(pageNumType.equalsIgnoreCase("simplenofirst") || pageNumType.equalsIgnoreCase("complexnofirst")) startPage++;

            /*if(repPageNumVPos.equalsIgnoreCase("top")){
                verticalPosition=0.96f;
            } else verticalPosition=0.05f;

            if( (!repPageNumHPos.equalsIgnoreCase("blank") && curPage>1) || (!repPageNumHPos.equalsIgnoreCase("blank") && curPage>1) ) {
                PdfContentByte cb = writer.getDirectContent();
                cb.saveState();
                cb.beginText();
                if (repPageNumHPos.equalsIgnoreCase("right"))
                    cb.moveText((float) (_doc.getPageSize().getWidth() * 0.96 - _doc.rightMargin()), (float) (_doc.getPageSize().getHeight() * verticalPosition));
                if (repPageNumHPos.equalsIgnoreCase("center"))
                    cb.moveText((float) (_doc.getPageSize().getWidth() * 0.5), (float) (_doc.getPageSize().getHeight() * verticalPosition));
                if (repPageNumHPos.equalsIgnoreCase("left"))
                    cb.moveText((float) (_doc.getPageSize().getWidth() * 0.05 + _doc.leftMargin()), (float) (_doc.getPageSize().getHeight() * verticalPosition));
                cb.setFontAndSize(pageBF, pageFontWeight);
                cb.showText("Лист " + curPage);
                curPage++;
                cb.endText();
                cb.restoreState();
            }*/


        if(curPage>startPage && (pageNumType.equalsIgnoreCase("simple") || pageNumType.equalsIgnoreCase("simplenofirst")) ) {

            float totalHeaderWidth = _doc.getPageSize().getWidth() - _doc.rightMargin() - _doc.leftMargin();

            try {
                table.setWidths(new int[]{16, 6, 24, 24, 6, 24, 24, 6});
                table.setTotalWidth(totalHeaderWidth);
                table.setLockedWidth(true);
                //table.getDefaultCell().setFixedHeight(50);
                table.getDefaultCell().setBorder(Rectangle.BOX);
                table.getDefaultCell().setBorderWidth(0);
                if (pageNumHPos.equalsIgnoreCase("left")) {
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(new Phrase(String.valueOf(writer.getPageNumber()), headerFont));
                } else{
                    table.addCell(header);
                    table.addCell(header);
                }
                table.addCell(header);


                if (pageNumHPos.equalsIgnoreCase("center")) {
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(new Phrase(String.valueOf(writer.getPageNumber()), headerFont));
                } else{
                    table.addCell(header);
                    table.addCell(header);
                    table.addCell(header);
                }
                table.addCell(header);

                if (pageNumHPos.equalsIgnoreCase("right")) {
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(new Phrase(String.valueOf(writer.getPageNumber()), headerFont));

                } else{
                    table.addCell(header);
                    table.addCell(header);
                    table.addCell(header);
                }
            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            }
        }

        if(curPage>startPage && (pageNumType.equalsIgnoreCase("complex") || pageNumType.equalsIgnoreCase("complexnofirst")) ) {

            float totalHeaderWidth = _doc.getPageSize().getWidth() - _doc.rightMargin() - _doc.leftMargin();

            try {
                table.setWidths(new int[]{16, 6, 24, 24, 6, 24, 24, 6});
                table.setTotalWidth(totalHeaderWidth);
                table.setLockedWidth(true);
                //table.getDefaultCell().setFixedHeight(50);
                table.getDefaultCell().setBorder(Rectangle.BOX);
                table.getDefaultCell().setBorderWidth(0);
                if (pageNumHPos.equalsIgnoreCase("left")) {
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(new Phrase(pageText + " " + writer.getPageNumber() + " из ", headerFont));

                    PdfPCell cell = new PdfPCell(Image.getInstance(total));
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    cell.setBorderWidth(0);
                    table.addCell(cell);
                } else{
                    table.addCell(header);
                    table.addCell(header);
                }
                table.addCell(header);

                if (pageNumHPos.equalsIgnoreCase("center")) {
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(new Phrase(pageText + " " + writer.getPageNumber() + " из ", headerFont));

                    PdfPCell cell = new PdfPCell(Image.getInstance(total));
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    cell.setBorderWidth(0);
                    table.addCell(cell);
                } else{
                    table.addCell(header);
                    table.addCell(header);
                }
                table.addCell(header);

                if (pageNumHPos.equalsIgnoreCase("right")) {

                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(new Phrase(pageText + " " + writer.getPageNumber() + " из ", headerFont));

                    PdfPCell cell = new PdfPCell(Image.getInstance(total));
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    cell.setBorderWidth(0);
                    table.addCell(cell);
                } else{
                    table.addCell(header);
                    table.addCell(header);
                }
            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            }

        }

        if(pageNumVPos.equalsIgnoreCase("top") && curPage>startPage && !pageNumType.equalsIgnoreCase("blank"))
            table.writeSelectedRows(0, -1,(float) (_doc.leftMargin()), _doc.getPageSize().getHeight() - (float) (_doc.topMargin()*0.5), writer.getDirectContent());

        if(pageNumVPos.equalsIgnoreCase("bottom") && curPage>startPage && !pageNumType.equalsIgnoreCase("blank"))
            table.writeSelectedRows(0, -1,(float) (_doc.leftMargin()), _doc.bottomMargin(), writer.getDirectContent());

        if (headerItems.size()>0 && curPage>1) drawHeader(writer,headerItems);

        curPage++;

    }

    /**
     * Fills out the total number of pages before the document is closed.
     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onCloseDocument(
     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
     */
    public void onCloseDocument(PdfWriter writer, Document document) {
        ColumnText.showTextAligned(total, Element.ALIGN_LEFT,
                new Phrase(String.valueOf(writer.getPageNumber() - 1), headerFont),
                2, 2, 0);
    }
}