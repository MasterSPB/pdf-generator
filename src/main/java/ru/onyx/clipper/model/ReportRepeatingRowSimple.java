package ru.onyx.clipper.model;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.onyx.clipper.data.PropertyGetter;
import ru.onyx.clipper.utils.ReportRegexUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by novikam on 16.07.14.
 */
public class ReportRepeatingRowSimple extends BaseReportObject{


    public ReportRepeatingRowSimple(Node tableNode,HashMap<String ,ReportBaseFont> fonts,BaseReportObject pParent,PropertyGetter pGetter) throws ParseException, DocumentException, IOException {
        _fonts = fonts;
        parent = pParent;
        propertyGetter = pGetter;
        String nodeName;
        Load(tableNode);
        NodeList cellsRep=null;

        NodeList childsList = tableNode.getChildNodes();

        for(int h=0;h<childsList.getLength();h++) {
            nodeName = childsList.item(h).getNodeName();
            Node node = childsList.item(h);
        }

        if(getPageName().length() > 0) {
            if(pGetter.GetPageCount(getPageName())!=0){
                int n = pGetter.GetPageCount(getPageName());
                for(int y=0;y<n;y++) {
                    for (int h = 0; h < childsList.getLength(); h++) {
                        nodeName = childsList.item(h).getNodeName();
                        Node node = childsList.item(h);

                        if (nodeName.equalsIgnoreCase("items")) {
                            NodeList cells = node.getChildNodes();
                            cellsRep = cells;
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
                        cellsRep = node.getChildNodes();
                    }
                }
            }
            if(items.size()==0) {
                for (int m = 0; m < cellsRep.getLength(); m++) {
                    nodeName = cellsRep.item(m).getNodeName();
                    if (nodeName.equalsIgnoreCase("cell")) {
                        NamedNodeMap attrObj = cellsRep.item(m).getAttributes();
                        String propName = parseAttribute(attrObj, "defaultnullvalue", "");
                        ReportCell cellToAdd = new ReportCell(cellsRep.item(m), _fonts, this, pGetter);
                        cellToAdd.setCustomText(propName);
                        items.add(cellToAdd);
                    }
                }
            }
        }
    }


    @Override
    public PdfPTable getPdfObject() throws DocumentException, ParseException, IOException{
        PdfPTable table = new PdfPTable(getColumns());
        if(getTotalWidth() >0) table.setTotalWidth(getTotalWidth());
        if(getWidthPercentage() >0) table.setWidthPercentage(getWidthPercentage());
        if(getWidthCellsPercentage() != null) table.setWidths(getWidthCellsPercentage());
        table.setHorizontalAlignment(getHorizontalAlignment());


        if(getSpacingAfter() >= 0) table.setSpacingAfter(getSpacingAfter());
        if(getSpacingBefore() >=0) table.setSpacingBefore(getSpacingBefore());


        for(int y=0;y<items.size(); y++) {
            PdfPCell obj = ((ReportCell)items.get(y)).getPdfObject();
            table.addCell(obj);
        }


        table.setComplete(true);
        return table;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
