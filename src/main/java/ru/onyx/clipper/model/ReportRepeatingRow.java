package ru.onyx.clipper.model;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.onyx.clipper.data.PropertyGetter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User: Alex
 * Date: 15.03.12
 * Time: 18:12
 */
public class ReportRepeatingRow extends BaseReportObject {
    PdfPTable header;
    public ReportRepeatingRow(Node tableNode,HashMap<String ,ReportBaseFont> fonts,BaseReportObject pParent,PropertyGetter pGetter) throws ParseException {
      _fonts = fonts;
      parent = pParent;
      propertyGetter = pGetter;
      String nodeName;
      Load(tableNode);

      NodeList childsList = tableNode.getChildNodes();

        for(int h=0;h<childsList.getLength();h++) {
            nodeName = childsList.item(h).getNodeName();
            Node node = childsList.item(h);

            if(nodeName.equalsIgnoreCase("header")) {
                NodeList headerChildList = node.getChildNodes();
                for(int j=0;j<headerChildList.getLength();j++) {
                    nodeName = headerChildList.item(j).getNodeName();
                    if(nodeName.equalsIgnoreCase("table"))
                        try {
                            header = (new ReportTable(headerChildList.item(j), _fonts, this, propertyGetter)).getPdfObject();
                            header.setTotalWidth(PageSize.A4.getWidth() * header.getWidthPercentage() / 100);
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }
        }


         if(getPageName().length() > 0) {
         int n = pGetter.GetPageCount(getPageName());
         for(int y=0;y<n;y++) {
             for(int h=0;h<childsList.getLength();h++) {
                 nodeName = childsList.item(h).getNodeName();
                 Node node = childsList.item(h);

                 if(nodeName.equalsIgnoreCase("items")) {
                     NodeList cells = node.getChildNodes();
                     for(int i=0;i<cells.getLength();i++) {
                         nodeName=cells.item(i).getNodeName();
                         if (nodeName.equalsIgnoreCase("cell")) {
                             NamedNodeMap attrObj = cells.item(i).getAttributes();
                             String propName = parseAttribute(attrObj, "property", "");
                             String textCell = pGetter.GetProperty(String.format("%s[%s].%s", getPageName(), y, propName));
                             SetAttribute(attrObj, "customtext", textCell);

                             items.add(new ReportCell(cells.item(i), _fonts, this, pGetter));
                         }
                     }
                 }
             }
           }
         }
     }

    public List<Object> getPdfTable() throws DocumentException, ParseException, IOException {
        PdfPTable table = new PdfPTable(getColumns());
        List<Object> repeatingRowObjects = new ArrayList<>();

        int firstPageRows = getRepRowFPageRows();
        int otherPageRows = getRepRowOtherPageRows();
        int lastUsedRow = 0;
        int totalCols = getColumns();
        int totalCells = items.size();
        int totalRows = totalCells/totalCols;
        float curTableHeight = 0f;
        float cellHeight=0f;
        float headerHeight = header.calculateHeights();
        float firstPageTblHeight = getRepRowFPageHeight();
        float otherPageTblHeight = getRepRowOtherPageHeight();
        boolean firstTblAdded=false; // flag to determine that first page is done
        boolean docComplete=false;
        boolean toBeSeparated=false; // this flag indicates whether the document is one-paged or multi-paged


        setTableParams(table);


        for (int i=0; i<totalRows; i++){
            //row iteration
            for(int j=0; j<totalCols; j++){
                //column iteration
                PdfPCell obj = ((ReportCell) items.get(i*totalCols+j)).getPdfObject();
                table.addCell(obj);
            }

            curTableHeight=table.calculateHeights();
            //System.out.println(table.getRowHeight(0));
            cellHeight=table.getRow(0).getMaxRowHeightsWithoutCalculating();
            if ( ( ( ((( i+1==firstPageRows) || ( i+1==totalRows) )) && (firstPageRows > 0 && otherPageRows > 0) ) ||
                   (curTableHeight+headerHeight+cellHeight > firstPageTblHeight && firstPageTblHeight > 0 && otherPageTblHeight > 0) ) && !firstTblAdded ) {
            //if current rownum is equal to number of rows of first page (in XML) or iteration has reached the end and it is a first page, than
                table.setHeaderRows(1);
                table.setComplete(true);
                lastUsedRow=i; //remember that row
                repeatingRowObjects.add(header); //add header before table
                repeatingRowObjects.add(table); //add the table to the list
                firstTblAdded=true; //say that first page is done
                table = new PdfPTable(getColumns()); //create new table and format it
                toBeSeparated=true;
                if(i+1==totalRows) docComplete=true; // This means that document fits on one page and there is no need to do any more actions

                setTableParams(table);

                if(getReplicateHeader().equals(Boolean.FALSE)) {
                headerHeight=0f;
                }
            }
            if ( ( ((i-lastUsedRow == otherPageRows || (i-lastUsedRow == otherPageRows) && i+2==totalRows) && (firstPageRows > 0 && otherPageRows > 0) ) ||
                    (curTableHeight+headerHeight+cellHeight > otherPageTblHeight && firstPageTblHeight > 0 && otherPageTblHeight > 0) || i+1==totalRows  )&& firstTblAdded && !docComplete){
            //if current rownum is equal to number of rows of other pages (in XML) or iteration has reached the end and it is a first page or the next element is the last and it is not a first page, than
                repeatingRowObjects.add(null); //add new page if this is not a first break

                table.setHeaderRows(0);
                table.setComplete(true);
                lastUsedRow=i; //remember the row
                if(getReplicateHeader().equals(Boolean.TRUE)) {
                    repeatingRowObjects.add(header); //add header before table on the other pages if flag is done
                }
                repeatingRowObjects.add(table); //add the table to the list
                table = new PdfPTable(getColumns()); //create new table
                setTableParams(table);
            }
        }
        if(!toBeSeparated){
            repeatingRowObjects.add(header); //add header before table
            table.setHeaderRows(0);
            table.setComplete(true);
            repeatingRowObjects.add(table);
        }
        return repeatingRowObjects;
    }

    protected void setTableParams(PdfPTable table){
        if(getTotalWidth() >0) table.setTotalWidth(getTotalWidth());
        if(getWidthPercentage() >0) table.setWidthPercentage(getWidthPercentage());
        if(getWidthCellsPercentage() != null) try {
            table.setWidths(getWidthCellsPercentage());
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        table.setHorizontalAlignment(getHorizontalAlignment());
        table.setTotalWidth(PageSize.A4.getWidth() * table.getWidthPercentage() / 100);

        if(getSpacingAfter() >= 0) table.setSpacingAfter(getSpacingAfter());
        if(getSpacingBefore() >=0) table.setSpacingBefore(getSpacingBefore());
    }


    @Override
    public PdfPTable getPdfObject() throws DocumentException, ParseException, IOException {
        throw new NotImplementedException();
    }
}
