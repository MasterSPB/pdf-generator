package ru.onyx.clipper.model;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.onyx.clipper.data.PropertyGetter;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import static ru.onyx.clipper.model.Report.*;
import static ru.onyx.clipper.model.Report.paragraph;

/**
 * Created by Пользователь on 04.04.2015.
 */
public class ReportForEach extends BaseReportObject  {


    public ReportForEach(Node tableNode, HashMap<String ,ReportBaseFont> fonts, BaseReportObject pParent, PropertyGetter pGetter) throws ParseException, DocumentException, IOException {
        _fonts = fonts;
        parent = pParent;
        propertyGetter = pGetter;
        Load(tableNode);
        //LoadItems(tableNode, fonts, this, pGetter);

        if(!getPageName().contains("$") && getPageNameRT() != null){
            setPageName(getPageNameRT()+getPageName());
        }

        NodeList childsList = tableNode.getChildNodes();

        if(getPageName().length() > 0) {
            int n = pGetter.GetPageCount(getPageName());
            if (n > -1) {
                for (int y = 0; y < n; y++) {
                    for (int h = 0; h < childsList.getLength(); h++) {
                        String nodeName = childsList.item(h).getNodeName();

                        Node node = childsList.item(h);
                        if(nodeName.equals(var)) {
                            String propName = getPropertyByTextContent(node.getTextContent());
                            String textValue = pGetter.GetProperty(String.format("%s[%s].%s", getPageName(), y, propName));
                            SetAttribute(node.getAttributes(), "value", textValue);
                            new ReportVar(node, _fonts, this, pGetter);
                        }
                        if(nodeName.equals(ifcondition)) {
                            NodeList ifNodeList = node.getChildNodes();
                            String propName = "";
                            for (int j = 0; j < ifNodeList.getLength(); j++) {
                                Node conditionNode = ifNodeList.item(j);
                                if(conditionNode.getNodeName().equals("condition")) {
                                    propName = getPropertyByTextContent(conditionNode.getTextContent());
                                }
                            }

                            String textValue = pGetter.GetProperty(String.format("%s[%s].%s", getPageName(), y, propName));
                            ReportConditionalStatements.parseIfStatement(node.getChildNodes(), pGetter, logicalcondition, elsecondition, paragraph, items, fonts, textValue);
                        }
                        // String propName = parseAttribute(attrObj, "property", "");
                        // String textCell = pGetter.GetProperty(String.format("%s[%s].%s", getPageName(), y, propName));
                        // SetAttribute(attrObj, "customtext", textCell);
                    }
                }
            }
        }
    }


    @Override
    public Element getPdfObject() throws DocumentException, ParseException, IOException {
        return null;
    }
}
