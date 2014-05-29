package ru.onyx.clipper.model;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import org.w3c.dom.Node;
import ru.onyx.clipper.data.PropertyGetter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * User: Alex
 * Date: 15.03.12
 * Time: 14:21
 */
public class ReportWordSplitter extends BaseReportObject {
    
    class DashedTable implements PdfPTableEvent {
        @Override
        public void tableLayout(PdfPTable table, float[][] widths,
                                float[] heights, int headerRows, int rowStart,
                                PdfContentByte[] canvases) {
            PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
            canvas.saveState();
            canvas.setLineCap(PdfContentByte.LINE_CAP_PROJECTING_SQUARE);
            canvas.setLineDash(new float[] {0.125f, 3.0f}, 5.0f);
            float llx = widths[0][0];
            float urx = widths[0][widths.length];
            for (int i = 0; i < heights.length; i++) {
                canvas.moveTo(llx, heights[i]);
                canvas.lineTo(urx, heights[i]);
            }
            for (int i = 0; i < widths.length; i++) {
                for (int j = 0; j < widths[i].length; j++) {
                    canvas.moveTo(widths[i][j], heights[i]);
                    canvas.lineTo(widths[i][j], heights[i+1]);
                }
            }
            canvas.stroke();
            canvas.restoreState();
        }
    }

    private ArrayList<ReportCell> items = new ArrayList<ReportCell>();

    public ReportWordSplitter(Node node,HashMap<String ,ReportBaseFont> fonts,BaseReportObject pParent,PropertyGetter pGetter) throws ParseException {

      _fonts = fonts;
      parent = pParent;
      propertyGetter = pGetter;
      Load(node);

        int paramMargin = 0;
        String param = propertyGetter.GetProperty(getPropertyName());

        String mode = getPropertyMode();
        if(mode != null) {      //Then we assume the property is date
           param = getDateProperty(mode,getPropertyName(),getDateFormat());
        }

        if(param == null) param = "";
        String paramAlign = getWordAlign();
         if(param.length() < getColumns()) {
          paramMargin = getColumns() - param.length();
         }
         for(int y=0;y<getColumns();y++) {
             ReportCell cell = new ReportCell(getMinimumHeight(),"",getVerticalTextAlignment(),getHorizontalTextAlignment(), getFontName(),getFontWeight(),getBorderWidth() ,getLeading() ,getPaddings(),getUseBorderPadding(),getBGColor(),getBorderColor(),pGetter,fonts);

             if(paramAlign.equals("right")) {
                 if(y >= paramMargin) {
                 String symbol = Character.toString(param.charAt(y-paramMargin));
                 cell = new ReportCell(getMinimumHeight(),symbol,getVerticalTextAlignment(),getHorizontalTextAlignment(), getFontName(),getFontWeight(),getBorderWidth(),getLeading(),getPaddings(),getUseBorderPadding(),getBGColor(),getBorderColor(),pGetter,fonts);
                 }
             }
             if(paramAlign.equals("left")) {
                 if(y < param.length()) {
                 String symbol = Character.toString(param.charAt(y));
                 cell = new ReportCell(getMinimumHeight(),symbol,getVerticalTextAlignment(),getHorizontalTextAlignment(), getFontName(),getFontWeight(),getBorderWidth(),getLeading(),getPaddings(),getUseBorderPadding(),getBGColor(),getBorderColor(),pGetter,fonts);
                 }
             }

             items.add(cell);
        }

    }

       public String getDateProperty(String part,String propertyName,String dateFormat) throws ParseException {
        Date s = propertyGetter.GetPropertyStringAsDate(propertyName,dateFormat);
        if (s != null) {
            Calendar ca1 = Calendar.getInstance();
            ca1.setTime(s);
            String ret= "";
            if(part.equalsIgnoreCase("day")) {
                ret = String.valueOf(ca1.get(Calendar.DAY_OF_MONTH));
            }
            if(part.equalsIgnoreCase("month")) {
                ret = String.valueOf(ca1.get(Calendar.MONTH));
                if(ret.length() == 1) ret = "0"+ret;
            }
            if(part.equalsIgnoreCase("year")) {
                ret = String.valueOf(ca1.get(Calendar.YEAR));
            }

            return ret;

        }
           return "";
    }


    public PdfPTable getPdfObject() throws DocumentException, ParseException, IOException {

        PdfPTable table = new PdfPTable(getColumns());
        if(getTotalWidth() >0) table.setTotalWidth(getTotalWidth());
        if(getWidthPercentage() >0) table.setWidthPercentage(getWidthPercentage());

        table.setHorizontalAlignment(getHorizontalAlignment());
        if(getBorderStyle() != null && getBorderStyle().equals("dotted")) {
           table.setTableEvent(new DashedTable());
           table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        }

        for (ReportCell item : items) {

            table.addCell(item.getPdfObject());
        }

        return table;
    }

}
