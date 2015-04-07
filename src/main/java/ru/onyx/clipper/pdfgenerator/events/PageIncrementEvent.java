package ru.onyx.clipper.pdfgenerator.events;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import ru.onyx.clipper.pdfgenerator.model.Report;

/**
 * Created by user on 01.08.14.
 */
public class PageIncrementEvent extends PdfPageEventHelper {
    Report rep;

    public PageIncrementEvent(Report report){
        rep = report;
    }

    public void onEndPage(PdfWriter writer, Document document) {
        rep.setCurPage(writer.getPageNumber());
    }


}
