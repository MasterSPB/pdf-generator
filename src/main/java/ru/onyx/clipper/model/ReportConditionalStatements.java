package ru.onyx.clipper.model;

import org.w3c.dom.NodeList;
import ru.onyx.clipper.data.PropertyGetter;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by anton on 14.05.14.
 */

public class ReportConditionalStatements {
    protected static void parseIfStatement(NodeList ifStatementItems, PropertyGetter pGetter, String logicalcondition, String elsecondition, String paragraph, List<BaseReportObject> items, HashMap<String, ReportBaseFont> fonts) {
        String[] operands;
        boolean conditionResult=false;

        for (int t = 0; t < ifStatementItems.getLength(); t++) {
            //iteration inside "if" statement
            String nodeName = ifStatementItems.item(t).getNodeName();

            if (nodeName.equals(logicalcondition)) { //if it is a condition...

                if (ifStatementItems.item(t).getTextContent().contains(" ")) { //...and it has spaces (correct syntax)
                    operands = ifStatementItems.item(t).getTextContent().split(" "); //split it into tokens
                    if (operands.length < 3) return; //if there is less than three tokens, it is not a correct statement
                } else return;//if there are no spaces, it is not a correct statement

                for (int i = 0; i < operands.length; i = i + 2) { //check every second token beginning from first
                    if (Character.toString(operands[i].charAt(0)).equals("$")) { //if it begins from "$" symbol, it needs to be overwritten by it's json value
                        operands[i] = pGetter.GetProperty(operands[i]);
                    }
                }

                if(operands[1].equals("eq") && operands[0].equals(operands[2])) conditionResult=true; //if we are checking tokens to be equal
                if(operands[1].equals("neq") && (operands[0]!=null && operands[2]!=null))
                    if(!operands[0].equals(operands[2])) conditionResult=true; //if we are checking tokens to be not equal
            }

            if (conditionResult && nodeName.equals(paragraph)) //now, if condition is TRUE, add all paragraphs to markup
                try {
                    items.add(new ReportParagraph(ifStatementItems.item(t), fonts, null, pGetter));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            if (!conditionResult && nodeName.equals(elsecondition)){ //if condition is false, find else statement...
                NodeList elseStatementItems = ifStatementItems.item(t).getChildNodes(); //...get its child nodes
                for (int i=0; i < elseStatementItems.getLength(); i++){
                    nodeName = elseStatementItems.item(i).getNodeName();
                    if(nodeName.equals(paragraph)){
                        try {
                            items.add(new ReportParagraph(ifStatementItems.item(t), fonts, null, pGetter)); //...and add paragraphs to markup from there
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}
