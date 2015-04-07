package ru.onyx.clipper.model;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.onyx.clipper.data.PropertyGetter;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;

/**
 * Created by Пользователь on 06.04.2015.
 */
public class ReportVar extends BaseReportObject {

    public ReportVar(Node node, HashMap<String ,ReportBaseFont> fonts, BaseReportObject pParent, PropertyGetter pGetter) throws ParseException, DocumentException, IOException {
        String[] operands = new String[0];

        NamedNodeMap attrObj = node.getAttributes();
        String name = parseAttribute(attrObj, "name", "");
        String value = parseAttribute(attrObj, "value", "");

        if(!node.getTextContent().equals("")) {
            NodeList nodeList = node.getChildNodes();
            for (int t = 0; t < nodeList.getLength(); t++) {
                if (nodeList.item(t).getTextContent().contains(" ")) { //...and it has spaces (correct syntax)
                    operands = nodeList.item(t).getTextContent().split(" "); //split it into tokens
                }

                for (int i = 0; i < operands.length; i = i + 2) {
                    if (Character.toString(operands[i].charAt(0)).equals("$")) {
                        operands[i] = pGetter.GetProperty(operands[i]);
                    } else if (Character.toString(operands[i].charAt(0)).equals("#")) {
                        String var = operands[i].substring(1, operands[i].length());
                        HashMap<String, String> varMap = BaseReportObject.getVarMap();
                        if (varMap.get(var) != null) {
                            operands[i] = varMap.get(var);
                        }
                    } else {
                        operands[i] = value;
                    }
                }
                if(operands[0]!=null&&operands[2]!=null) {
                    if (operands[1].equals("+")) {
                        value = String.valueOf(Double.parseDouble(operands[0]) + Double.parseDouble(operands[2]));
                        varMap.put(name, value);
                    }
                }
            }
        } else {
            if(!"".equals(name) &&  !"".equals(value)) {
                varMap.put(name, value);
            }
        }

    }

    @Override
    public Element getPdfObject() throws DocumentException, ParseException, IOException {
        return null;
    }
}
