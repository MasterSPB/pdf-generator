package ru.onyx.clipper.model;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.w3c.dom.Node;
import ru.onyx.clipper.data.PropertyGetter;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;


/**
 * User: Alex
 * Date: 14.03.12
 * Time: 19:15
 */
public class ReportTable extends BaseReportObject {

    //private ArrayList<ReportCell> items = new ArrayList<ReportCell>();

    public ReportTable(Node node, HashMap<String, ReportBaseFont> fonts, BaseReportObject pParent, PropertyGetter pGetter) throws ParseException {
        _fonts = fonts;
        parent = pParent;
        propertyGetter = pGetter;
        Load(node);
        LoadItems(node, fonts, this, pGetter);
    }

    public PdfPTable getPdfObject() throws DocumentException, ParseException, IOException {

        PdfPTable table = new PdfPTable(this.columns);
        if(getTotalWidth() >0) table.setTotalWidth(getTotalWidth());
        if(getWidthPercentage() >0) table.setWidthPercentage(getWidthPercentage());

         if(getSpacingAfter() != -1) table.setSpacingAfter(getSpacingAfter());
         if(getSpacingBefore() != -1) table.setSpacingBefore(getSpacingBefore());

        table.setKeepTogether(getKeepTogether());
        table.setHorizontalAlignment(getHorizontalAlignment());

        if(getWidthCellsPercentage() != null) {
        if(getWidthCellsPercentage().length > 0) {
            float[] cellsPercs = new float[getWidthCellsPercentage().length];
            for(int y=0;y<getWidthCellsPercentage().length;y++) {
                cellsPercs[y] = getWidthCellsPercentage()[y];
            }
            table.setWidths(cellsPercs);
        }
        }
        for (BaseReportObject item : items) {
            PdfPCell obj = ((ReportCell) item).getPdfObject();
            table.addCell(obj);
        }
        table.setComplete(true);
        return table;
    }
}
