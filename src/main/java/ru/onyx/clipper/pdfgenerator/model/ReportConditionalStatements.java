package ru.onyx.clipper.pdfgenerator.model;

import com.itextpdf.text.DocumentException;
import org.w3c.dom.NodeList;
import ru.onyx.clipper.pdfgenerator.data.PropertyGetter;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.lang.String;

/**
 * Created by anton on 14.05.14.
 */

public class ReportConditionalStatements {
    protected static void parseIfStatement(NodeList ifStatementItems, PropertyGetter pGetter, String logicalcondition, String elsecondition, String paragraph, List<BaseReportObject> items, HashMap<String, ReportBaseFont> fonts, Report report) throws IOException, DocumentException {
        String[] operands;
        boolean conditionResult=false;

        for (int t = 0; t < ifStatementItems.getLength(); t++) {
            //iteration inside "if" statement
            String nodeName = ifStatementItems.item(t).getNodeName();

            if (nodeName.equals(logicalcondition)) { //if it is a condition...

                if (ifStatementItems.item(t).getTextContent().contains(" ")) { //...and it has spaces (correct syntax)
                    operands = ifStatementItems.item(t).getTextContent().split(" "); //split it into tokens
  //                  if (operands.length < 3) return; //if there is less than three tokens, it is not a correct statement
                } else return;//if there are no spaces, it is not a correct statement

                for (int i = 0; i < operands.length; i = i + 2) { //check every second token beginning from first
                    if (Character.toString(operands[i].charAt(0)).equals("$")) { //if it begins from "$" symbol, it needs to be overwritten by it's json value
                        operands[i] = pGetter.GetProperty(operands[i]);
                    }
                }
                if(operands.length == 3 && operands[0]!=null && operands[2]!=null) {
                    if (operands[1].equals("eq") && operands[0].equals(operands[2]))
                        conditionResult = true; //if we are checking tokens to be equal
                    if (operands[1].equals("neq") && (operands[0] != null && operands[2] != null))
                        if (!operands[0].equals(operands[2]))
                            conditionResult = true; //if we are checking tokens to be not equal
                    if (operands[1].equals("ge") && ((Double.parseDouble(operands[0]) >= Double.parseDouble(operands[2]))))
                        conditionResult = true; //if we are checking tokens to be more or equals
                    if (operands[1].equals("le") && ((Double.parseDouble(operands[0]) <= Double.parseDouble(operands[2]))))
                        conditionResult = true; //if we are checking tokens to be less or equals
                    if (operands[1].equals("gt") && ((Double.parseDouble(operands[0]) > Double.parseDouble(operands[2]))))
                        conditionResult = true; //if we are checking tokens to be more
                    if (operands[1].equals("lt") && ((Double.parseDouble(operands[0]) < Double.parseDouble(operands[2]))))
                        conditionResult = true; //if we are checking tokens to be less
                    if (operands[1].equals("LengthIs") && (operands[0].length() == Integer.parseInt(operands[2]))) {
                        conditionResult = true; //if we are checking first token to be exact length as stated
                    }
                } else if (operands.length == 2) {
                    if (operands[1].equals("isNotNull") && operands[0]==null) {
                        conditionResult = false;
                    } else {
                        conditionResult = true;
                    }
                }
            }

            if(conditionResult){
                switchNodeName(nodeName,ifStatementItems,pGetter, items,fonts,t,report);
            }

            if (!conditionResult && nodeName.equals(elsecondition)){ //if condition is false, find else statement...
                NodeList elseStatementItems = ifStatementItems.item(t).getChildNodes(); //...get its child nodes
                for (int i=0; i < elseStatementItems.getLength(); i++){
                    nodeName = elseStatementItems.item(i).getNodeName();
                    switchNodeName(nodeName,ifStatementItems,pGetter, items,fonts, t,report);
                }
            }
        }

    }

    private static void switchNodeName(String nodeName, NodeList ifStatementItems, PropertyGetter pGetter, List<BaseReportObject> items, HashMap<String, ReportBaseFont> fonts, int t,Report report) throws IOException, DocumentException {
        switch (nodeName){
            case "date": {
                try{
                    items.add(new ReportDate(ifStatementItems.item(t),fonts,null,pGetter));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                break;
            }
            case "dateday": {
                try{
                    items.add(new ReportDateDay(ifStatementItems.item(t),fonts,null,pGetter));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                break;
            }
            case "datemonth": {
                try{
                    items.add(new ReportDateMonth(ifStatementItems.item(t),fonts,null,pGetter));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                break;
            }
            case "dateyear": {
                try{
                    items.add(new ReportDateYear(ifStatementItems.item(t),fonts,null,pGetter));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                break;
            }
            case "image":{
                try{
                    items.add(new ReportImage(ifStatementItems.item(t),fonts,null,pGetter));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                break;
            }
            case "moneychunk": {
                try{
                    items.add(new ReportMoneyChunk(ifStatementItems.item(t),fonts,null,pGetter));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                break;
            }
            case "newpage": {
                try{
                    items.add(new ReportNewPage());
                }catch (ParseException e){
                    e.printStackTrace();
                }
                break;
            }
            case "newsection": {
                try{
                    items.add(new ReportNewSection(ifStatementItems.item(t)));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                break;
            }
            case "paragraph": {
                try{
                    items.add(new ReportParagraph(ifStatementItems.item(t),fonts,null,pGetter,report));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                break;
            }
            case "phrase": {
                try{
                    items.add(new ReportPhrase(ifStatementItems.item(t),fonts,null,pGetter,report));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                break;
            }
            case "repeatingrow": {
                try{
                    items.add(new ReportRepeatingRow(ifStatementItems.item(t),fonts,null,pGetter,report,report._doc));
                }catch (ParseException e){
                    e.printStackTrace();
                }catch (DocumentException de){
                    de.printStackTrace();
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }
                break;
            }

            case "table": {
                try{
                    items.add(new ReportTable(ifStatementItems.item(t),fonts,null,pGetter,null));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                break;
            }
            case "wordsplitter": {
                try{
                    items.add(new ReportWordSplitter(ifStatementItems.item(t),fonts,null,pGetter));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                break;
            }
            case "chunk": {
                try{
                    items.add(new ReportChunk(ifStatementItems.item(t),fonts,null,pGetter,report));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                break;
            }
            case "cell": {
                try{
                    items.add(new ReportCell(ifStatementItems.item(t),fonts,null,pGetter,report));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                break;
            }
        }
    }

}
