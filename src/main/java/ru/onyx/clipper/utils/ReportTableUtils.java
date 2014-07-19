package ru.onyx.clipper.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by anton on 27.06.14.
 */
public class ReportTableUtils {
    public static void setExactWidthFromPercentage(PdfPTable table, Document _doc) {
        table.setTotalWidth((_doc.getPageSize().getWidth() - _doc.leftMargin() - _doc.rightMargin()) * table.getWidthPercentage()/100);
        table.setLockedWidth(true);
    }

    public static Float getTableVerticalSize(PdfPTable table) throws DocumentException, ParseException, IOException {
        float overallHeight=0.0f;
        for(PdfPRow curRow : table.getRows()) {
            float maxHeight = 0.0f;
            for(PdfPCell curCell : curRow.getCells()) {
                if(curCell != null && curCell.getHeight()>maxHeight) maxHeight=curCell.getHeight();
            }
            overallHeight+=maxHeight;
        }
        return overallHeight;
    }

    public static Float getRowVerticalSize(PdfPRow row) {
        float maxHeight=0.0f;
        for(PdfPCell curCell : row.getCells()) {
            maxHeight = 0.0f;
            if(curCell != null && curCell.getHeight()>maxHeight) maxHeight=curCell.getHeight();
        }
        return maxHeight;
    }
}
