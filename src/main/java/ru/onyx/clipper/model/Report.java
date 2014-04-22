package ru.onyx.clipper.model;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.*;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ru.onyx.clipper.data.PropertyGetter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: MasterSPB
 * Date: 13.03.12
 * Time: 18:24
 */
public class Report {
    public static final String pagenumvpos = "pagenumvpos";
    public static final String pagenumhpos = "pagenumhpos";
    public static final String A4 = "a4";
    public static final String portrait = "portrait";
    public static final String landscape = "landscape";
    public static final String marginleft = "marginleft";
    public static final String margintop = "margintop";
    public static final String marginright = "marginright";
    public static final String marginbottom = "marginbottom";
    public static final String pagesize = "pagesize";
    public static final String orientation = "orientation";
    public static final String name = "name";
    public static final String path = "path";
    public static final String margin_value = "5";
    public static final String paragraph = "paragraph";
    public static final String table = "table";
    public static final String repeatingrow = "repeatingrow";
    public static final String dateparagraph = "dateparagraph";
    public static final String wordsplitter = "wordsplitter";
    public static final String newpage = "newpage";
    public static final String chunk = "chunk";
    public static final String phrase = "phrase";
    public static final String cell = "cell";
    public static final String day = "day";
    public static final String month = "month";
    public static final String year = "year";
    public static final String image = "image";
    public static final String pagefont = "pagefont";
    public static final String header = "header";
    public static final String ifcondition = "if";
    public static final String elsecondition = "else";
    public static final String logicalcondition = "condition";

    private static int curPage=1;

    private HashMap<String, byte[]> fontBodies;
    private HashMap<String, ReportBaseFont> fonts = new HashMap<String, ReportBaseFont>();
    private ArrayList<BaseReportObject> items = new ArrayList<BaseReportObject>();
    private ArrayList<BaseReportObject> headerItems = new ArrayList<BaseReportObject>();
    private String repPageNumHPos ="blank";
    private String pageFont="arial";
    private String repPageNumVPos="bottom";

    Document _doc = new Document();

    public ArrayList<BaseReportObject> getItems() {
        return items;
    }

    /**
     * Load Document Markup
     *
     * @param xmlMarkup
     * @param pFontBodies
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws DocumentException
     */
    public void LoadMarkup(String xmlMarkup, HashMap<String, byte[]> pFontBodies, PropertyGetter pGetter) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, DocumentException, ParseException {
        fontBodies = pFontBodies;

        // step 2
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlMarkup));
        org.w3c.dom.Document xmlDoc = db.parse(is);

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(new SkeletonNameSpaceContext());
        XPathExpression expr = xpath.compile("reportDefinition/fonts/baseFont");
        NodeList result = (NodeList) expr.evaluate(xmlDoc, XPathConstants.NODESET);


        expr = xpath.compile("reportDefinition/report");
        org.w3c.dom.Node reportNode = (org.w3c.dom.Node) expr.evaluate(xmlDoc, XPathConstants.NODE);

        NamedNodeMap attrs = reportNode.getAttributes();
        float marginLeft = Float.parseFloat(parseAttribute(attrs, marginleft, margin_value));
        float marginTop = Float.parseFloat(parseAttribute(attrs, margintop, margin_value));
        float marginRight = Float.parseFloat(parseAttribute(attrs, marginright, margin_value));
        float marginBottom = Float.parseFloat(parseAttribute(attrs, marginbottom, margin_value));
        String pageSize = parseAttribute(attrs, pagesize, A4);
        String orientation = parseAttribute(attrs, Report.orientation, portrait);
        pageFont = parseAttribute(attrs,Report.pagefont, "arial");
        repPageNumHPos = parseAttribute(attrs, Report.pagenumhpos, "blank");
        repPageNumVPos = parseAttribute(attrs, Report.pagenumvpos, "bottom");


        if (pageSize.equalsIgnoreCase(A4) && orientation.equalsIgnoreCase(portrait)) {
            _doc.setPageSize(PageSize.A4);
        }

        if (pageSize.equalsIgnoreCase(A4) && orientation.equalsIgnoreCase(landscape)) {
            _doc.setPageSize(PageSize.A4.rotate());

        }

        _doc.setMargins(marginLeft, marginRight, marginTop, marginBottom);

        for (int i = 0; i < result.getLength(); i++) {
            String fontName = result.item(i).getAttributes().getNamedItem(name).getTextContent();
            String fontPath = result.item(i).getAttributes().getNamedItem(path).getTextContent();
            ReportBaseFont baseFont = new ReportBaseFont(fontName, fontPath, fontBodies.get(fontName));
            fonts.put(fontName, baseFont);

        }

        XPathExpression exprRepParagraph = xpath.compile("reportDefinition/report/items/*");
        NodeList repChilds = (NodeList) exprRepParagraph.evaluate(xmlDoc, XPathConstants.NODESET);

        for (int t = 0; t < repChilds.getLength(); t++) {
            String nodeName = repChilds.item(t).getNodeName();

            if (nodeName.equals(header)){
                NodeList headerChildList = repChilds.item(t).getChildNodes();
                parseHeader(headerChildList, pGetter);
            }

            if (nodeName.equals(paragraph)) {
                items.add(new ReportParagraph(repChilds.item(t), fonts, null, pGetter));
            }
            if (nodeName.equals(table)) {
                items.add(new ReportTable(repChilds.item(t), fonts, null, pGetter));
            }
            if (nodeName.equals(repeatingrow)) {
                items.add(new ReportRepeatingRow(repChilds.item(t), fonts, null, pGetter));
            }
            if (nodeName.equals(dateparagraph)) {
                items.add(new ReportDate(repChilds.item(t), fonts, null, pGetter));
            }
            if (nodeName.equals(wordsplitter)) {
                items.add(new ReportWordSplitter(repChilds.item(t), fonts, null, pGetter));
            }
            if (nodeName.equals(newpage)) {
                items.add(new ReportNewPage());
            }

            if (nodeName.equals(ifcondition)) {
                NodeList ifStatementChildren = repChilds.item(t).getChildNodes();
                parseIfStatement(ifStatementChildren, pGetter);
            }
        }
    }


    public HashMap<String, byte[]> getFontBodies() {
        return fontBodies;
    }

    public static String parseAttribute(NamedNodeMap attrs, String attrName, String defaultValue) {
        if (attrs == null) return defaultValue;
        Node attrObj = attrs.getNamedItem(attrName);
        if (attrObj == null) return defaultValue;
        return attrObj.getTextContent();
    }

    public byte[] GetDocument() throws DocumentException, ParseException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter wr = PdfWriter.getInstance(_doc, byteArrayOutputStream);
        wr.setRgbTransparencyBlending(true);
        BaseFont pageBF = BaseFont.createFont("/fonts/"+pageFont+".ttf", BaseFont.IDENTITY_H, true); //font for page numbers


        _doc.open();
        int size = items.size(); // size element to set a last page

        for (BaseReportObject item : items) {

            if (item instanceof ReportNewPage) {

                if(!repPageNumHPos.equalsIgnoreCase("blank") && curPage>1) {
                    setPageNumber(pageBF,10,wr);
                } else if (curPage==1) curPage++;

                _doc.newPage();
            }
            else if (item instanceof ReportRepeatingRow) {

                for(Object reportRepeatingRowItem : ((ReportRepeatingRow) item).getPdfTable())
                {
                    if(reportRepeatingRowItem==null){

                        if(!repPageNumHPos.equalsIgnoreCase("blank") && curPage>1) {
                            setPageNumber(pageBF,10,wr); // Sets the page number
                            drawHeader(wr,headerItems); // Draws header on all pages but first and last
                        } else if (curPage==1) curPage++;

                        _doc.newPage();
                    }
                    else _doc.add((com.itextpdf.text.Element) reportRepeatingRowItem);
                }

            } else if (item.getPdfObject() != null) _doc.add(item.getPdfObject());

            if (--size==0){
                setPageNumber(pageBF, 10, wr); // Sets the last page number
                drawHeader(wr,headerItems); // Draws header on the last page
            }
        }


        PdfAction ac = PdfAction.gotoLocalPage(1, new
                PdfDestination(PdfDestination.XYZ, 0, _doc.getPageSize().getHeight(), 1f), wr);
        wr.setOpenAction(ac);
        _doc.close();
        wr.close();
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    protected void drawHeader(PdfWriter writer, ArrayList<BaseReportObject> headerItems){
    // draws header inside margins
        ColumnText ct = new ColumnText(writer.getDirectContent());
        ct.setSimpleColumn(_doc.leftMargin(),_doc.topMargin(), _doc.getPageSize().getWidth() - _doc.rightMargin(),_doc.getPageSize().getHeight() * 0.99f);

        for (BaseReportObject headerItem : headerItems){
            try {
                ct.addElement(headerItem.getPdfObject());
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            ct.go();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    protected void parseHeader(NodeList headerChildList, PropertyGetter pGetter) {
    // this function parses header tag
        String nodeName;
        for (int j = 0; j < headerChildList.getLength(); j++) {
            nodeName = headerChildList.item(j).getNodeName();
            if (nodeName.equalsIgnoreCase("paragraph")) {
                try {
                    headerItems.add(new ReportParagraph(headerChildList.item(j), fonts, null, pGetter));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void parseIfStatement(NodeList ifStatementItems, PropertyGetter pGetter) {
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

            if (conditionResult==true && nodeName.equals(paragraph)) //now, if condition is TRUE, add all paragraphs to markup
                try {
                    items.add(new ReportParagraph(ifStatementItems.item(t), fonts, null, pGetter));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            if (conditionResult==false && nodeName.equals(elsecondition)){ //if condition is false, find else statement...
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


    protected void setPageNumber(BaseFont baseFont, int fontSize, PdfWriter writer){
        float verticalPosition;

        if(repPageNumVPos.equalsIgnoreCase("top")){
            verticalPosition=0.96f;
        } else verticalPosition=0.05f;

        PdfContentByte cb = writer.getDirectContent();
        cb.saveState();
        cb.beginText();
        if(repPageNumHPos.equalsIgnoreCase("right")) cb.moveText((float) (_doc.getPageSize().getWidth()*0.96 - _doc.rightMargin() ), (float) (_doc.getPageSize().getHeight()*verticalPosition));
        if(repPageNumHPos.equalsIgnoreCase("center")) cb.moveText((float) (_doc.getPageSize().getWidth()*0.5 ), (float) (_doc.getPageSize().getHeight()*verticalPosition));
        if(repPageNumHPos.equalsIgnoreCase("left")) cb.moveText((float) (_doc.getPageSize().getWidth()*0.05 + _doc.leftMargin()), (float) (_doc.getPageSize().getHeight()*verticalPosition));
        cb.setFontAndSize(baseFont, fontSize);
        cb.showText("Лист " + curPage);
        curPage++;
        cb.endText();
        cb.restoreState();
    }
}