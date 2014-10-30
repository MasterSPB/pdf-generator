package ru.onyx.clipper.model;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.onyx.clipper.data.PropertyGetter;
import ru.onyx.clipper.utils.ReportRegexUtils;
import ru.onyx.clipper.utils.ReportTableUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.text.ParseException;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Alex
 * Date: 15.03.12
 * Time: 18:12
 */
public class ReportRepeatingRow extends BaseReportObject {
    PdfPTable header;
    PdfPTable footer;
    PdfPTable finalLine;

    private int totalRows = 0;

    Map aggrMap;
    public ReportRepeatingRow(Node tableNode,HashMap<String ,ReportBaseFont> fonts,BaseReportObject pParent,PropertyGetter pGetter, Document _doc) throws ParseException, DocumentException, IOException {
        _fonts = fonts;
        parent = pParent;
        propertyGetter = pGetter;
        String nodeName;
        Load(tableNode);

        if(!getPageName().contains("$") && getPageNameRT() != null){
            setPageName(getPageNameRT()+getPageName());
        }

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
                            header = (new ReportTable(headerChildList.item(j), _fonts, this, propertyGetter,null)).getPdfObject();
                            ReportTableUtils.setExactWidthFromPercentage(header, _doc);
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }

            if(nodeName.equalsIgnoreCase("footer")) {
                NodeList footerChildList = node.getChildNodes();
                for(int j=0;j<footerChildList.getLength();j++) {
                    nodeName = footerChildList.item(j).getNodeName();
                    if(nodeName.equalsIgnoreCase("table"))
                        try {
                            ReportTable footerTable = new ReportTable(footerChildList.item(j), _fonts, this, propertyGetter,null);
                            footer = footerTable.getPdfObject();
                            ReportTableUtils.setExactWidthFromPercentage(footer, _doc);
                            aggrMap = footerTable.getTableAggrProps();
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }

            if(nodeName.equalsIgnoreCase("final")) {
                NodeList finalChildList = node.getChildNodes();
                for(int j=0;j<finalChildList.getLength();j++) {
                    nodeName = finalChildList.item(j).getNodeName();
                    if(nodeName.equalsIgnoreCase("table"))
                        try {
                            ReportTable finalTable = new ReportTable(finalChildList.item(j), _fonts, this, propertyGetter,null);
                            finalLine = finalTable.getPdfObject();
                            ReportTableUtils.setExactWidthFromPercentage(finalLine, _doc);
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
            if(n > -1) {
                for (int y = 0; y < n; y++) {
                    for (int h = 0; h < childsList.getLength(); h++) {
                        nodeName = childsList.item(h).getNodeName();
                        Node node = childsList.item(h);

                        if (nodeName.equalsIgnoreCase("items")) {
                            NodeList cells = node.getChildNodes();
                            ArrayList<BaseReportObject> itemsTemp = new ArrayList<BaseReportObject>();
                            int cellCounter = 0;
                            for (int i = 0; i < cells.getLength(); i++) {
                                nodeName = cells.item(i).getNodeName();
                                if (nodeName.equalsIgnoreCase("cell")) {
                                    NamedNodeMap attrObj = cells.item(i).getAttributes();
                                    String expression = parseAttribute(attrObj, "expression", null);
                                    if (expression != null && !expression.equals("")) {
                                        String propName = parseAttribute(attrObj, "property", "");
                                        String textCell = pGetter.GetProperty(String.format("%s[%s].%s", getPageName(), y, propName));
                                        SetAttribute(attrObj, "customtext", textCell);
                                        if (expression.equalsIgnoreCase("eq")) {
                                            Pattern pat = ReportRegexUtils.getRegex(getOperandType(), getExpressionOperand(), getQuartIndex());
                                            Matcher mat = pat.matcher(textCell);
                                            if (mat.matches()) {
                                                itemsTemp.add(new ReportCell(cells.item(i), _fonts, this, pGetter));
                                                cellCounter++;
                                            } else {
                                                break;
                                            }
                                        }
                                    } else {
                                        String propName = parseAttribute(attrObj, "property", "");
                                        String textCell = pGetter.GetProperty(String.format("%s[%s].%s", getPageName(), y, propName));
                                        SetAttribute(attrObj, "customtext", textCell);
                                        itemsTemp.add(new ReportCell(cells.item(i), _fonts, this, pGetter));
                                        cellCounter++;
                                    }
                                }
                                if (cellCounter == getColumns()) {
                                    for (int k = 0; k < itemsTemp.size(); k++) {
                                        items.add(itemsTemp.get(k));
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }else if(n == -1){
                for (int h = 0; h < childsList.getLength(); h++) {
                    nodeName = childsList.item(h).getNodeName();
                    Node node = childsList.item(h);

                    if (nodeName.equalsIgnoreCase("items")) {
                        NodeList cells = node.getChildNodes();
                        ArrayList<BaseReportObject> itemsTemp = new ArrayList<BaseReportObject>();
                        int cellCounter = 0;
                        for (int i = 0; i < cells.getLength(); i++) {
                            nodeName = cells.item(i).getNodeName();
                            if (nodeName.equalsIgnoreCase("cell")) {
                                NamedNodeMap attrObj = cells.item(i).getAttributes();
                                String expression = parseAttribute(attrObj, "expression", null);
                                if (expression != null && !expression.equals("")) {
                                    String propName = parseAttribute(attrObj, "property", "");
                                    String textCell = pGetter.GetProperty(getPageName()+"."+propName);
                                    SetAttribute(attrObj, "customtext", textCell);
                                    if (expression.equalsIgnoreCase("eq")) {
                                        Pattern pat = ReportRegexUtils.getRegex(getOperandType(), getExpressionOperand(), getQuartIndex());
                                        Matcher mat = pat.matcher(textCell);
                                        if (mat.matches()) {
                                            itemsTemp.add(new ReportCell(cells.item(i), _fonts, this, pGetter));
                                            cellCounter++;
                                        } else {
                                            break;
                                        }
                                    }
                                } else {
                                    String propName = parseAttribute(attrObj, "property", "");
                                    String textCell = pGetter.GetProperty(getPageName()+"."+propName);
                                    SetAttribute(attrObj, "customtext", textCell);
                                    itemsTemp.add(new ReportCell(cells.item(i), _fonts, this, pGetter));
                                    cellCounter++;
                                }
                            }
                            if (cellCounter == getColumns()) {
                                for (int k = 0; k < itemsTemp.size(); k++) {
                                    items.add(itemsTemp.get(k));
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public ReportRepeatingRow(Node tableNode,HashMap<String ,ReportBaseFont> fonts,BaseReportObject pParent,PropertyGetter pGetter) throws ParseException, DocumentException, IOException {
        _fonts = fonts;
        parent = pParent;
        propertyGetter = pGetter;
        String nodeName;
        Load(tableNode);

        NodeList childsList = tableNode.getChildNodes();

        for(int h=0;h<childsList.getLength();h++) {
            nodeName = childsList.item(h).getNodeName();
            Node node = childsList.item(h);
        }

        if(getPageName().length() > 0) {
            int n = pGetter.GetPageCount(getPageName());
            if(n!=-1) {
                for (int y = 0; y < n; y++) {
                    for (int h = 0; h < childsList.getLength(); h++) {
                        nodeName = childsList.item(h).getNodeName();
                        Node node = childsList.item(h);

                        if (nodeName.equalsIgnoreCase("items")) {
                            NodeList cells = node.getChildNodes();
                            ArrayList<BaseReportObject> itemsTemp = new ArrayList<BaseReportObject>();
                            int cellCounter = 0;
                            for (int i = 0; i < cells.getLength(); i++) {
                                nodeName = cells.item(i).getNodeName();
                                if (nodeName.equalsIgnoreCase("cell")) {
                                    NamedNodeMap attrObj = cells.item(i).getAttributes();
                                    String expression = parseAttribute(attrObj, "expression", null);
                                    if (expression != null && !expression.equals("")) {
                                        String propName = parseAttribute(attrObj, "property", "");
                                        String textCell = pGetter.GetProperty(String.format("%s[%s].%s", getPageName(), y, propName));
                                        SetAttribute(attrObj, "customtext", textCell);
                                        if (expression.equalsIgnoreCase("eq")) {
                                            String qi = parseAttribute(attrObj, "quartindex", "");
                                            qi = pGetter.GetProperty(qi);
                                            String ot = parseAttribute(attrObj, "optype", "");
                                            String eo = parseAttribute(attrObj, "expoperand", "");
                                            Pattern pat = ReportRegexUtils.getRegex(ot, eo, qi);
                                            Matcher mat = pat.matcher(textCell);
                                            if (mat.matches()) {
                                                itemsTemp.add(new ReportCell(cells.item(i), _fonts, this, pGetter));
                                                cellCounter++;
                                            } else {
                                                break;
                                            }
                                        }
                                    } else {
                                        String propName = parseAttribute(attrObj, "property", "");
                                        String textCell = pGetter.GetProperty(String.format("%s[%s].%s", getPageName(), y, propName));
                                        SetAttribute(attrObj, "customtext", textCell);
                                        itemsTemp.add(new ReportCell(cells.item(i), _fonts, this, pGetter));
                                        cellCounter++;
                                    }
                                }
                                if (cellCounter == getColumns()) {
                                    for (int k = 0; k < itemsTemp.size(); k++) {
                                        items.add(itemsTemp.get(k));
                                    }
                                    break;
                                }
                            }
                        }


                    }
                }
            }else{
                for (int h = 0; h < childsList.getLength(); h++) {
                    nodeName = childsList.item(h).getNodeName();
                    Node node = childsList.item(h);

                    if (nodeName.equalsIgnoreCase("items")) {
                        NodeList cells = node.getChildNodes();
                        ArrayList<BaseReportObject> itemsTemp = new ArrayList<BaseReportObject>();
                        int cellCounter = 0;
                        for (int i = 0; i < cells.getLength(); i++) {
                            nodeName = cells.item(i).getNodeName();
                            if (nodeName.equalsIgnoreCase("cell")) {
                                NamedNodeMap attrObj = cells.item(i).getAttributes();
                                String expression = parseAttribute(attrObj, "expression", null);
                                if (expression != null && !expression.equals("")) {
                                    String propName = parseAttribute(attrObj, "property", "");
                                    String textCell = pGetter.GetProperty(getPageName()+ "."+ propName);
                                    SetAttribute(attrObj, "customtext", textCell);
                                    if (expression.equalsIgnoreCase("eq")) {
                                        String qi = parseAttribute(attrObj, "quartindex", "");
                                        qi = pGetter.GetProperty(qi);
                                        String ot = parseAttribute(attrObj, "optype", "");
                                        String eo = parseAttribute(attrObj, "expoperand", "");
                                        Pattern pat = ReportRegexUtils.getRegex(ot, eo, qi);
                                        Matcher mat = pat.matcher(textCell);
                                        if (mat.matches()) {
                                            itemsTemp.add(new ReportCell(cells.item(i), _fonts, this, pGetter));
                                            cellCounter++;
                                        } else {
                                            break;
                                        }
                                    }
                                } else {
                                    String propName = parseAttribute(attrObj, "property", "");
                                    String textCell = pGetter.GetProperty(getPageName() + "." + propName);
                                    SetAttribute(attrObj, "customtext", textCell);
                                    itemsTemp.add(new ReportCell(cells.item(i), _fonts, this, pGetter));
                                    cellCounter++;
                                }
                            }
                            if (cellCounter == getColumns()) {
                                for (int k = 0; k < itemsTemp.size(); k++) {
                                    items.add(itemsTemp.get(k));
                                }
                                break;
                            }
                        }
                    }


                }
            }
        }
    }

    public List<Object> getPdfTable(float spaceLeft, Document _doc) throws DocumentException, ParseException, IOException {
        List<Object> repeatingRowObjects = new ArrayList<>();

        int lastUsedRow = 0;
        int totalCols = getColumns();
        int totalCells = items.size();
        totalRows = totalCells/totalCols;
        int minFreeSpaceAfter = getMinFreeSpaceAfter();
        float curTableHeight = 0f;
        float cellHeight=0f;
        float headerHeight;
        float heightLeft;
        if( header!=null) {
            headerHeight = header.calculateHeights();
        } else headerHeight = 0.0f;
        float footerHeight;
        if(footer!=null) {
            footerHeight = footer.calculateHeights();
        } else footerHeight = 0.0f;
        float otherPageTblHeight = getRepRowOtherPageHeight();
        boolean firstTblAdded=false; // flag to determine that first page is done
        boolean onePaged=false;
        boolean docComplete=false;

        PdfPTable table = new PdfPTable(getColumns());
        setTableParams(table, _doc);
        ReportTableUtils.setExactWidthFromPercentage(table, _doc);
        String[] cellsFormat = new String[totalCols];

        if(totalCells>0) {
            for (int colNum = 0; colNum < totalCols; colNum++) {
                if (items.get(colNum).getStringformat() == null) {
                    cellsFormat[colNum] = "";
                } else cellsFormat[colNum] = items.get(colNum).getStringformat();
            }

            for (int i = 0; i < totalRows; i++) {
                //row iteration
                cellHeight = 0;
                for (int j = 0; j < totalCols; j++) {               //column iteration
                    if (items.get(i * totalCols + j).getNumerator())
                        ((ReportCell) items.get(i * totalCols + j)).setText(String.valueOf(i + 1));
                    PdfPCell obj = ((ReportCell) items.get(i * totalCols + j)).getPdfObject();
                    table.addCell(obj);
                }


                ReportTableUtils.setExactWidthFromPercentage(table, _doc);
                curTableHeight = table.getTotalHeight();

                cellHeight = table.getRow(table.getRows().size() - 1).getMaxRowHeightsWithoutCalculating();
                heightLeft = (totalRows - i) * cellHeight;
                if (getReplicateHeader().equals(Boolean.FALSE)) {
                    headerHeight = 0f;
                }
                if (getReplicateFooter().equals(Boolean.FALSE)) {
                    footerHeight = 0f;
                }

                if ((curTableHeight + headerHeight + footerHeight + cellHeight + table.getRows().size() * getBorderWidth() > spaceLeft
                        || spaceLeft - curTableHeight - headerHeight - footerHeight - cellHeight - table.getRows().size() * getBorderWidth() < minFreeSpaceAfter) && !firstTblAdded) {
                    // if one of the limits has been found while trying to add a first page
                    firstTblAdded = true;
                    if (i + 2 == totalRows) { //this means that cur. row is penultimate and the next one needs to be moved to a next page. The document is one-paged and is larger than limits.

                        drawTable(table, repeatingRowObjects, cellsFormat);

                        repeatingRowObjects.add(null);
                        table = new PdfPTable(getColumns()); //create new table
                        setTableParams(table, _doc);
                        curTableHeight = 0;

                    } else if (i + 1 == totalRows) { // else it fits exactly on one page
                        onePaged = true;
                        drawTable(table, repeatingRowObjects, cellsFormat);

                    } else { //else it needs to be continued on a next page
                        if ((totalRows - i) * cellHeight < minFreeSpaceAfter) {
                            drawTable(table, repeatingRowObjects, cellsFormat);
                            repeatingRowObjects.add(null);
                            table = new PdfPTable(getColumns()); //create new table
                            setTableParams(table, _doc);
                            curTableHeight = 0;
                        }
                    }
                } else if (i + 1 == totalRows && !firstTblAdded) { //this means that cur. row is penultimate and the next one needs to be moved to a next page. The document is one-paged and is smaller than limits
                    firstTblAdded = true;
                    onePaged = true;
                    drawTable(table, repeatingRowObjects, cellsFormat);

                } else if (curTableHeight + headerHeight + footerHeight + cellHeight + table.getRows().size() * getBorderWidth() > spaceLeft && firstTblAdded) {
                    // if one of the limits has been found while trying to add NOT a first page
                    if (i + 2 == totalRows) { //this means that cur. row is penultimate and the next one needs to be moved to a next page.
                        drawTable(table, repeatingRowObjects, cellsFormat);

                        repeatingRowObjects.add(null);

                        table = new PdfPTable(getColumns()); //create new table
                        setTableParams(table, _doc);
                        curTableHeight = 0;
                    } else { //else it needs to be continued on a next page
                        drawTable(table, repeatingRowObjects, cellsFormat);
                        repeatingRowObjects.add(null);
                        table = new PdfPTable(getColumns()); //create new table
                        setTableParams(table, _doc);
                        curTableHeight = 0;
                        spaceLeft = otherPageTblHeight;
                        spaceLeft = otherPageTblHeight;
                    }
                } else if (firstTblAdded && heightLeft > _doc.getPageSize().getHeight() - _doc.bottomMargin() - _doc.topMargin() - curTableHeight - table.getRows().size() * getBorderWidth() - headerHeight - footerHeight - minFreeSpaceAfter) {

                    if (i + 2 == totalRows &&
                            _doc.getPageSize().getHeight() - _doc.bottomMargin() - _doc.topMargin() - curTableHeight - table.getRows().size() * getBorderWidth() - headerHeight - footerHeight < minFreeSpaceAfter) {
                        drawTable(table, repeatingRowObjects, cellsFormat);
                        repeatingRowObjects.add(null);
                        table = new PdfPTable(getColumns()); //create new table
                        setTableParams(table, _doc);

                        curTableHeight = 0;
                    } else if (i + 1 == totalRows) { //this means the time to add finalizer
                        drawTable(table, repeatingRowObjects, cellsFormat);
                        if (finalLine != null) repeatingRowObjects.add(finalLine);
                    }
                } else if (i + 1 == totalRows) { //this means the time to add finalizer
                    drawTable(table, repeatingRowObjects, cellsFormat);
                    if (finalLine != null) repeatingRowObjects.add(finalLine);
                }



            /*if((curTableHeight+headerHeight+footerHeight+cellHeight+table.getRows().size()*getBorderWidth() > firstPageTblHeight && firstPageTblHeight > 0 && otherPageTblHeight > 0 || i+1==totalRows) && !firstTblAdded) {
                //if current table fits to a first page, then
                table.setHeaderRows(0);
                table.setComplete(true);
                lastUsedRow=i; //remember that row
                if(header!=null) repeatingRowObjects.add(header); //add header before table if there is one
                repeatingRowObjects.add(table); //add the table to the list

                if(footer!=null) {
                    PdfPTable tempFooter = makeAggrRow(table,cellsFormat);
                        if(getReplicateFooter().equals(Boolean.TRUE)) {
                        repeatingRowObjects.add(tempFooter);
                    }
                }

                firstTblAdded=true; //say that first page is done

                if(i+1==totalRows) {
                    docComplete=true; // This means that document fits on one page and there is no need to do any more actions
                    if(finalLine!=null) repeatingRowObjects.add(finalLine);
                    if(spaceLeft - curTableHeight - headerHeight < minFreeSpaceAfter){
                        repeatingRowObjects.add(null);
                    }
                } else {
                    repeatingRowObjects.add(null);
                    table = new PdfPTable(getColumns()); //create new table and format it
                    setTableParams(table, _doc);
                    curTableHeight=0;

                    if(getReplicateHeader().equals(Boolean.FALSE)) {
                        headerHeight=0f;
                    }
                    if(getReplicateFooter().equals(Boolean.FALSE)) {
                        footerHeight=0f;
                    }
                }
            }

            if(( (curTableHeight+headerHeight+footerHeight+cellHeight+table.getRows().size()*getBorderWidth() > otherPageTblHeight && firstPageTblHeight > 0 && otherPageTblHeight > 0) || i+1==totalRows) && firstTblAdded && !docComplete) {
            //if this is not the first page and table has enough size or ends
                table.setHeaderRows(0);
                table.setComplete(true);
                lastUsedRow=i; //remember the row

                if(header!=null && getReplicateHeader().equals(Boolean.TRUE)) {
                    repeatingRowObjects.add(header); //add header before table on the other pages if flag is done
                }
                repeatingRowObjects.add(table); //add the table to the list
                if(footer!=null) {
                    PdfPTable tempFooter = makeAggrRow(table,cellsFormat);
                    if (getReplicateFooter().equals(Boolean.TRUE)) {
                        repeatingRowObjects.add(tempFooter);
                    }
                }
                if(i+1!=totalRows) repeatingRowObjects.add(null);
                table = new PdfPTable(getColumns()); //create new table
                setTableParams(table, _doc);
            }

            if(i+1==totalRows) {
                docComplete = true;
                if(finalLine!=null) repeatingRowObjects.add(finalLine);
            }

            if(i+2==totalRows && (_doc.getPageSize().getHeight() -_doc.bottomMargin() - _doc.topMargin() - curTableHeight - table.getRows().size()*getBorderWidth() - headerHeight - footerHeight < minFreeSpaceAfter)) {
                table.setHeaderRows(0);
                table.setComplete(true);
                if(header!=null && getReplicateHeader().equals(Boolean.TRUE)) {
                    repeatingRowObjects.add(header); //add header before table on the other pages if flag is done
                }
                repeatingRowObjects.add(table); //add the table to the list
                if(footer!=null) {
                    PdfPTable tempFooter = makeAggrRow(table, cellsFormat);
                    if (getReplicateFooter().equals(Boolean.TRUE)) {
                        repeatingRowObjects.add(tempFooter);
                    }
                }
                repeatingRowObjects.add(null);
                table = new PdfPTable(getColumns()); //create new table
                setTableParams(table, _doc);
            }
        }*/
            }
        } else {
            if(header!=null) repeatingRowObjects.add(header); //add header before table if there is one
            if(footer!=null) {
                repeatingRowObjects.add(footer);
            }
        }
        return repeatingRowObjects;
    }

    private void drawTable(PdfPTable table, List<Object> repeatingRowObjects, String[] cellsFormat) {
        table.setHeaderRows(0);
        table.setComplete(true);
        if(header!=null) repeatingRowObjects.add(header); //add header before table if there is one
        repeatingRowObjects.add(table); //add the table to the list
        if(footer!=null) {
            PdfPTable tempFooter = makeAggrRow(table,cellsFormat);
            if(getReplicateFooter().equals(Boolean.TRUE)) {
                repeatingRowObjects.add(tempFooter);
            }
        }
    }

    protected void setTableParams(PdfPTable table, Document _doc){
        if(getTotalWidth() >0) table.setTotalWidth(getTotalWidth());
        if(getWidthPercentage() >0) {
            table.setWidthPercentage(getWidthPercentage());
            ReportTableUtils.setExactWidthFromPercentage(table, _doc);
        }
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


    private PdfPTable makeAggrRow(PdfPTable table, String[] cellsFormat) {

        PdfPTable tempFooter = new PdfPTable(footer);
        PdfPCell[] tempCells = tempFooter.getRow(0).getCells(); //save footer cells to get access later
        PdfPCell[] tempTableCells = table.getRow(0).getCells();
        tempFooter.flushContent();
        double[] aggrRes = new double[50];
        String[] aggrType = new String[50];
        final int aggrRowsCount = table.getRows().size();
        final Iterator<Integer> cursor = aggrMap.keySet().iterator();
        int aggrResIndex=0;
        while (cursor.hasNext()) {
            final Integer key = cursor.next();
            if(aggrMap.get(key).toString().toLowerCase().equals("sum")) {
                for (int j = 0; j < aggrRowsCount; j++) {
                    PdfPCell[] cells = table.getRow(j).getCells();
                    if(cells[key-1].getPhrase().getContent().toString().length()==0) {
                        aggrType[aggrResIndex] = "int";
                        continue;
                    }
                    else if(cells[key-1].getPhrase().getContent().toString().contains(".") || cells[key-1].getPhrase().getContent().toString().contains(",")) {
                        aggrType[aggrResIndex] = "float";
                        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
                        try {
                            Number number = format.parse(cells[key - 1].getPhrase().getContent().toString());
                            aggrRes[aggrResIndex] += number.doubleValue();
                        } catch (ParseException e) {
                            aggrRes[aggrResIndex] += 0;
                        }
                    }
                    /*else if(cells[key-1].getPhrase().getContent().toString().contains(",")) {
                        aggrType[aggrResIndex]="float";
                        //String my_new_str = cells[key - 1].getPhrase().getContent().toString().replaceAll(",",".");
                        aggrRes[aggrResIndex] += Float.parseFloat(my_new_str);
                    }*/
                    else if(cells[key-1].getPhrase().getContent().toString().matches("[A-z][a-z]")) {

                    }
                    else {
                        aggrType[aggrResIndex] = "int";
                        aggrRes[aggrResIndex] += Float.parseFloat(cells[key - 1].getPhrase().getContent().toString());
                    }
                }
            }
            aggrResIndex++;
        }

        final Iterator<Integer> cursor2 = aggrMap.keySet().iterator();
        aggrResIndex=0;
        int k=0;
        while (cursor2.hasNext()) { //while aggr columns are needed to be added to footer
            final Integer key = cursor2.next(); //get next index of aggr column
            for (; k < footer.getNumberOfColumns(); k++) {
                if (tempCells[k] != null) { //if columns are not blank (colspan is not used)
                    if (k == key - 1) { //if current column is aggr column
                        if(aggrType[aggrResIndex].equals("float")) {
                            tempCells[k].setPhrase(new Phrase(String.valueOf(String.format(cellsFormat[k],aggrRes[aggrResIndex])), new Font(tempTableCells[k].getPhrase().getFont()))); //write aggr value with table's font
                        }
                        if(aggrType[aggrResIndex].equals("int")) {
                            tempCells[k].setPhrase(new Phrase(String.valueOf((int) aggrRes[aggrResIndex]), new Font(tempTableCells[k].getPhrase().getFont()))); //write aggr value with table's font
                        }
                        tempCells[k].setHorizontalAlignment(tempTableCells[k].getHorizontalAlignment());

                        break;
                    }
                }
            }
            aggrResIndex++;
        }

        for(k=0;k<footer.getNumberOfColumns(); k++) { //make a temp footer using previously saved cells with new info
            if(tempCells[k]!=null) tempFooter.addCell(tempCells[k]);
        }

        return tempFooter;
    }


    @Override
    public PdfPTable getPdfObject() throws DocumentException, ParseException, IOException {
        throw new NotImplementedException();
    }

    public int getTotalRows() {
        return totalRows;
    }
}
