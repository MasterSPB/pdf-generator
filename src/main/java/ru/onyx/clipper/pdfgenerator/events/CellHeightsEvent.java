package ru.onyx.clipper.pdfgenerator.events;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;

/**
 * Created by anton on 26.06.14.
 */
class CellHeightsEvent implements PdfPCellEvent {

    private float height=0.0f;

    public void cellLayout(PdfPCell cell, Rectangle rect,
                           PdfContentByte[] canvas) {
        PdfContentByte cb = canvas[PdfPTable.BACKGROUNDCANVAS];
        cb.saveState();

        height=rect.getHeight();
        cb.rectangle(rect.getLeft(), rect.getBottom(),
                rect.getWidth(), rect.getHeight());
        cb.fill();
        cb.restoreState();
    }
}
