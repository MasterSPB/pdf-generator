package ru.onyx.clipper.pdfgenerator.model;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.onyx.clipper.pdfgenerator.data.PropertyGetter;
import ru.onyx.clipper.pdfgenerator.utils.ReportRegexUtils;
import ru.onyx.clipper.pdfgenerator.utils.ReportTableUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: ViktorZahar
 * Date: 07.04.2015
 *
 *  Balanced table
 */
public class ReportRepeatingRowBalanced extends BaseReportObject {
    private PdfPTable header;
    private PdfPTable footer;
    private PdfPTable finalLine;
    private Locale locale;

    private int totalRows = 0;

    Map aggrMap;
    private int parts_count = 2;

    public ReportRepeatingRowBalanced(Node tableNode, HashMap<String, ReportBaseFont> fonts, BaseReportObject pParent, PropertyGetter pGetter, Report report, Document _doc) throws ParseException, DocumentException, IOException {
        _fonts = fonts;
        parent = pParent;
        propertyGetter = pGetter;
        this.report = report;
        String nodeName;
        Load(tableNode);

        if (getAggrFunctionLocale() != null) {
            locale = new Locale(getAggrFunctionLocale());
        } else {
            locale = new Locale("en");
        }

        if (!getPageName().contains("$") && getPageNameRT() != null) {
            setPageName(getPageNameRT() + getPageName());
        }

        NodeList childsList = tableNode.getChildNodes();

        for (int h = 0; h < childsList.getLength(); h++) {
            nodeName = childsList.item(h).getNodeName();
            Node node = childsList.item(h);

            if (nodeName.equalsIgnoreCase("header")) {
                NodeList headerChildList = node.getChildNodes();
                for (int j = 0; j < headerChildList.getLength(); j++) {
                    nodeName = headerChildList.item(j).getNodeName();
                    if (nodeName.equalsIgnoreCase("table"))
                        try {
                            header = (new ReportTable(headerChildList.item(j), _fonts, this, propertyGetter, null)).getPdfObject();
                            ReportTableUtils.setExactWidthFromPercentage(header, _doc);
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }

            if (nodeName.equalsIgnoreCase("footer")) {
                NodeList footerChildList = node.getChildNodes();
                for (int j = 0; j < footerChildList.getLength(); j++) {
                    nodeName = footerChildList.item(j).getNodeName();
                    if (nodeName.equalsIgnoreCase("table"))
                        try {
                            ReportTable footerTable = new ReportTable(footerChildList.item(j), _fonts, this, propertyGetter, null);
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

            if (nodeName.equalsIgnoreCase("final")) {
                NodeList finalChildList = node.getChildNodes();
                for (int j = 0; j < finalChildList.getLength(); j++) {
                    nodeName = finalChildList.item(j).getNodeName();
                    if (nodeName.equalsIgnoreCase("table"))
                        try {
                            ReportTable finalTable = new ReportTable(finalChildList.item(j), _fonts, this, propertyGetter, null);
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

        if (getPageName().length() > 0) {
            int n = pGetter.GetPageCount(getPageName());
            if (n > -1) {
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

                                    NamedNodeMap attrObj = cells.item(i).getAttributes(); // get Cell attribute from item

                                    if (attrObj.getLength() == 0) { // Если cell не имеет атрибутов то проверяем вложенные элементы и проверяем есть ли там chunk (вложенный в paragraph)

                                        NodeList childNode = cells.item(i).getChildNodes();
                                        for (int z = 0; z < childNode.getLength(); z++) {
                                            String childNodeName = childNode.item(z).getNodeName();
                                            if (childNodeName.equalsIgnoreCase("paragraph")) {
                                                NodeList paragraphChideNode = childNode.item(z).getChildNodes();
                                                for (int b = 0; b < paragraphChideNode.getLength(); b++) {
                                                    if (paragraphChideNode.item(b).getNodeName().equalsIgnoreCase("chunk")) {
                                                        attrObj = paragraphChideNode.item(b).getAttributes(); // Если мы нашли chunk, то берем его аттрибуты и устанавливаем с помощью метода SetAttribute
                                                        String propName = parseAttribute(attrObj, "property", "");
                                                        String textCell = pGetter.GetProperty(String.format("%s[%s].%s", getPageName(), y, propName));
                                                        SetAttribute(attrObj, "customtext", textCell);
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    String expression = parseAttribute(attrObj, "expression", null);
                                    if (expression != null && !expression.equals("")) {
                                        String propName = parseAttribute(attrObj, "property", "");
                                        String textCell = pGetter.GetProperty(String.format("%s[%s].%s", getPageName(), y, propName));
                                        SetAttribute(attrObj, "customtext", textCell);
                                        if (expression.equalsIgnoreCase("eq")) {
                                            Pattern pat = ReportRegexUtils.getRegex(getOperandType(), getExpressionOperand(), getQuartIndex());
                                            Matcher mat = pat.matcher(textCell);
                                            if (mat.matches()) {
                                                itemsTemp.add(new ReportCell(cells.item(i), _fonts, this, pGetter, report));
                                                cellCounter++;
                                            } else {
                                                break;
                                            }
                                        }
                                    } else {
                                        String propName = parseAttribute(attrObj, "property", "");
                                        String textCell = pGetter.GetProperty(String.format("%s[%s].%s", getPageName(), y, propName));
                                        SetAttribute(attrObj, "customtext", textCell);
                                        itemsTemp.add(new ReportCell(cells.item(i), _fonts, this, pGetter, report));
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
            } else if (n == -1) {
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
                                    String textCell = pGetter.GetProperty(getPageName() + "." + propName);
                                    SetAttribute(attrObj, "customtext", textCell);
                                    if (expression.equalsIgnoreCase("eq")) {
                                        Pattern pat = ReportRegexUtils.getRegex(getOperandType(), getExpressionOperand(), getQuartIndex());
                                        Matcher mat = pat.matcher(textCell);
                                        if (mat.matches()) {
                                            itemsTemp.add(new ReportCell(cells.item(i), _fonts, this, pGetter, report));
                                            cellCounter++;
                                        } else {
                                            break;
                                        }
                                    }
                                } else {
                                    String propName = parseAttribute(attrObj, "property", "");
                                    String textCell = pGetter.GetProperty(getPageName() + "." + propName);
                                    SetAttribute(attrObj, "customtext", textCell);
                                    itemsTemp.add(new ReportCell(cells.item(i), _fonts, this, pGetter, report));
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

    public PdfPTable getPdfTable(float spaceLeft, Document _doc) throws DocumentException, ParseException, IOException {
        List<Object> repeatingRowObjects = new ArrayList<>();

        int lastUsedRow = 0;
        int totalCols = getColumns();
        int totalCells = items.size();
        totalRows = totalCells / totalCols;
        int minFreeSpaceAfter = getMinFreeSpaceAfter();
        float curTableHeight = 0f;
        float cellHeight = 0f;
        float headerHeight;
        float heightLeft;
        if (header != null) {
            headerHeight = header.calculateHeights();
        } else headerHeight = 0.0f;
        float footerHeight;
        if (footer != null) {
            footerHeight = footer.calculateHeights();
        } else footerHeight = 0.0f;
        float otherPageTblHeight = getRepRowOtherPageHeight();
        boolean firstTblAdded = false; // flag to determine that first page is done
        boolean onePaged = false;
        boolean docComplete = false;


        PdfPTable table = new PdfPTable(getColumns());
        PdfPTable table2 = new PdfPTable(getColumns());
        setTableParams(table, _doc);
        setTableParams(table2, _doc);
        ReportTableUtils.setExactWidthFromPercentage(table, _doc);
        ReportTableUtils.setExactWidthFromPercentage(table2, _doc);
        table.setTotalWidth(table.getTotalWidth());
        table2.setTotalWidth(table2.getTotalWidth());
        String[] cellsFormat = new String[totalCols];
        int half_count = totalRows / parts_count;
        if ((totalRows % parts_count) > 0) {
            half_count++;
        }

        if (totalCells > 0) {
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
                    try {
                        PdfPCell obj = ((ReportCell) items.get(i * totalCols + j)).getPdfObject();
                        if (i < half_count)
                            table.addCell(obj);
                        else table2.addCell(obj);
                    } catch (ClassCastException ex) {
                        //ex.printStackTrace();
                    }
                }
                if ((totalRows % parts_count) > 0) {
                    for (int emp=0;emp<getColumns();emp++){
                        table2.addCell(new PdfPCell());
                    }
                }
                ReportTableUtils.setExactWidthFromPercentage(table, _doc);
                ReportTableUtils.setExactWidthFromPercentage(table2, _doc);
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
                    drawTable(table2, repeatingRowObjects, cellsFormat);
                    if (finalLine != null) repeatingRowObjects.add(finalLine);

                } else if (curTableHeight + headerHeight + footerHeight + cellHeight + table.getRows().size() * getBorderWidth() > spaceLeft && firstTblAdded) {
                    // if one of the limits has been found while trying to add NOT a first page
                    if (i + 2 == totalRows) { //this means that cur. row is penultimate and the next one needs to be moved to a next page.
                        drawTable(table, repeatingRowObjects, cellsFormat);
                        drawTable(table2, repeatingRowObjects, cellsFormat);
                        repeatingRowObjects.add(null);

                        table = new PdfPTable(getColumns()); //create new table
                        setTableParams(table, _doc);
                        curTableHeight = 0;
                    } else { //else it needs to be continued on a next page
                        drawTable(table, repeatingRowObjects, cellsFormat);
                        drawTable(table2, repeatingRowObjects, cellsFormat);
                        repeatingRowObjects.add(null);
                        table = new PdfPTable(getColumns()); //create new table
                        setTableParams(table, _doc);
                        curTableHeight = 0;
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
            }
        } else {
            if (header != null) {
                repeatingRowObjects.add(header); //add header before table if there is one
                repeatingRowObjects.add(header);
            }
            if (footer != null) {
                repeatingRowObjects.add(footer);
            }
        }
        PdfPTable combo_table = new PdfPTable(3);
        combo_table.setWidthPercentage(104);
        java.util.List<Object> pdfTables = repeatingRowObjects;
        int i = 0;
        while (i < pdfTables.size()/2) {
            PdfPTable leftTable = (PdfPTable) pdfTables.get(i);
            PdfPCell left_cell = new PdfPCell(leftTable);
            left_cell.setBorder(0);
            combo_table.addCell(left_cell);
            PdfPCell middle_cell = new PdfPCell();
            middle_cell.setBorder(0);

            combo_table.addCell(middle_cell);
            PdfPTable rightTable = (PdfPTable) pdfTables.get(i+2);
            PdfPCell right_cell = new PdfPCell(rightTable);
            right_cell.setBorder(0);
            combo_table.addCell(right_cell);
            i++;
        }
        combo_table.setWidths(new int[]{30,0,30});
        return combo_table;
    }

    private void drawTable(PdfPTable table, List<Object> repeatingRowObjects, String[] cellsFormat) {
        table.setHeaderRows(0);
        table.setComplete(true);
        if (header != null) repeatingRowObjects.add(header); //add header before table if there is one
        repeatingRowObjects.add(table); //add the table to the list
        if (footer != null) {
            PdfPTable tempFooter = makeAggrRow(table, cellsFormat);
            if (getReplicateFooter().equals(Boolean.TRUE)) {
                repeatingRowObjects.add(tempFooter);
            }
        }
    }

    protected void setTableParams(PdfPTable table, Document _doc) {
        if (getTotalWidth() > 0) table.setTotalWidth(getTotalWidth());
        if (getWidthPercentage() > 0) {
            table.setWidthPercentage(getWidthPercentage());
            ReportTableUtils.setExactWidthFromPercentage(table, _doc);
        }
        if (getWidthCellsPercentage() != null) try {
            table.setWidths(getWidthCellsPercentage());
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        table.setHorizontalAlignment(getHorizontalAlignment());
        table.setTotalWidth(PageSize.A4.getWidth() * table.getWidthPercentage() / 100);

        if (getSpacingAfter() >= 0) table.setSpacingAfter(getSpacingAfter());
        if (getSpacingBefore() >= 0) table.setSpacingBefore(getSpacingBefore());
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
        int aggrResIndex = 0;
        while (cursor.hasNext()) {
            final Integer key = cursor.next();
            if (aggrMap.get(key).toString().toLowerCase().equals("sum")) {
                for (int j = 0; j < aggrRowsCount; j++) {
                    PdfPCell[] cells = table.getRow(j).getCells();
                    String cellContents = cells[key - 1].getPhrase().getContent().toString();
                    if (cellContents.length() == 0) {
                        aggrType[aggrResIndex] = "int";
                    } else if (cellContents.contains(".") || cellContents.contains(",")) {
                        aggrType[aggrResIndex] = "float";
                        try {
                            // Unify representation to a "double-look"
                            cellContents = cellContents.replaceAll(" ", "").replace(",", ".");
                            double number = Double.parseDouble(cellContents);
                            aggrRes[aggrResIndex] += number;
                        } catch (Exception e) {
                            aggrRes[aggrResIndex] += 0;
                            e.printStackTrace();
                        }
                    } else if (cellContents.matches("[A-z][a-z]")) {

                    } else {
                        aggrType[aggrResIndex] = "int";
                        aggrRes[aggrResIndex] += Float.parseFloat(cellContents);
                    }
                }
            }
            aggrResIndex++;
        }

        final Iterator<Integer> cursor2 = aggrMap.keySet().iterator();
        aggrResIndex = 0;
        int k = 0;
        while (cursor2.hasNext()) { //while aggr columns are needed to be added to footer
            final Integer key = cursor2.next(); //get next index of aggr column
            for (; k < footer.getNumberOfColumns(); k++) {
                if (tempCells[k] != null) { //if columns are not blank (colspan is not used)
                    if (k == key - 1) { //if current column is aggr column
                        if (aggrType[aggrResIndex].equals("float")) {
                            Phrase phrase = new Phrase(String.valueOf(String.format(locale, cellsFormat[k], aggrRes[aggrResIndex])), new Font(tempTableCells[k].getPhrase().getFont())); //write aggr value with table's font and Russian locale
                            tempCells[k].setPhrase(phrase);
                        }
                        if (aggrType[aggrResIndex].equals("int")) {
                            tempCells[k].setPhrase(new Phrase(String.valueOf((int) aggrRes[aggrResIndex]), new Font(tempTableCells[k].getPhrase().getFont()))); //write aggr value with table's font
                        }
                        tempCells[k].setHorizontalAlignment(tempTableCells[k].getHorizontalAlignment());

                        break;
                    }
                }
            }
            aggrResIndex++;
        }

        for (k = 0; k < footer.getNumberOfColumns(); k++) { //make a temp footer using previously saved cells with new info
            if (tempCells[k] != null) tempFooter.addCell(tempCells[k]);
        }

        return tempFooter;
    }


    @Override
    protected String getPageName() {
        String pn = super.getPageName();
        if (getPageNameRT() != null) return getPageNameRT() + pn;
        return pn;
    }

    @Override
    public PdfPTable getPdfObject() throws DocumentException, ParseException, IOException {
        throw new NotImplementedException();
    }

    public int getTotalRows() {
        return totalRows;
    }
}
