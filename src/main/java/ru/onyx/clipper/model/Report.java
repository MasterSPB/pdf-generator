package ru.onyx.clipper.model;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ru.onyx.clipper.data.PropertyGetter;
import ru.onyx.clipper.events.HeaderEvent;
import ru.onyx.clipper.events.PageIncrementEvent;
import ru.onyx.clipper.utils.ReportDocumentUtils;
import ru.onyx.clipper.utils.ReportTableUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: MasterSPB
 * Date: 13.03.12
 * Time: 18:24
 */
public class Report {
    public static final String pagenumtype = "pagenumtype";
    public static final String pagenumvpos = "pagenumvpos";
    public static final String pagenumhpos = "pagenumhpos";
    public static final String A4 = "a4";
    public static final String A3 = "a3";
    public static final String A2 = "a2";
    public static final String A1 = "a1";
    public static final String A0 = "a0";

    public static final String B4 = "b4";
    public static final String B3 = "b3";
    public static final String B2 = "b2";
    public static final String B1 = "b1";
    public static final String B0 = "b0";
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
    public static final String moneychunk = "moneychunk";
    public static final String phrase = "phrase";
    public static final String cell = "cell";
    public static final String day = "day";
    public static final String month = "month";
    public static final String year = "year";
    public static final String image = "image";
    public static final String pagefont = "pagefont";
    public static final String header = "header";
    public static final String lowerrunningtitle="lowerrunningtitle";
    public static final String ifcondition = "if";
    public static final String elsecondition = "else";
    public static final String logicalcondition = "condition";
    public static final String pagefontweight = "pagefontweight";
    public static final String pageheader = "pageheader";
    public static final String pagetext = "pagetext";
    public static final String repeatingtemplate = "repeatingtemplate";


    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int _curPage){
        curPage = _curPage;
    }

    private static int curPage=1;
    private static int pageFontWeight;

    private float marginLeft;
    private float marginRight;
    private float marginBottom;
    private float marginTop;

    private HashMap<String, byte[]> fontBodies;
    private HashMap<String, ReportBaseFont> fonts = new HashMap<String, ReportBaseFont>();
    private ArrayList<BaseReportObject> items = new ArrayList<BaseReportObject>();

    public ArrayList<BaseReportObject> getHeaderItems() {
        return headerItems;
    }

    private ArrayList<BaseReportObject> headerItems = new ArrayList<BaseReportObject>();

    public String getRepPageNumHPos() {
        return repPageNumHPos;
    }

    public String getPageFontName() {
        return pageFontName;
    }

    public String getRepPageNumVPos() {
        return repPageNumVPos;
    }

    public String getPageNumType() {
        return pageNumType;
    }

    private String repPageNumHPos;
    private String pageFontName;
    private String repPageNumVPos;
    private String pageNumType;
    private String pageHeader;
    private String pageSize;
    private String pageOrientation;
    private String lowerRunningTitle;

    private float spaceLeft;

    public String getPageText() {
        return pageText;
    }

    private String pageText="";

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

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlMarkup));
        org.w3c.dom.Document xmlDoc = db.parse(is);

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(new SkeletonNameSpaceContext());

        LoadFonts(xpath, xmlDoc);

        XPathExpression expr = xpath.compile("reportDefinition/report");
        org.w3c.dom.Node reportNode = (org.w3c.dom.Node) expr.evaluate(xmlDoc, XPathConstants.NODE);

        NamedNodeMap attrs = reportNode.getAttributes();

        initAttrs(attrs);

        setPageSize(pageSize, pageOrientation);
        _doc.setMargins(marginLeft, marginRight, marginTop, marginBottom);
        spaceLeft  = _doc.getPageSize().getHeight() - _doc.topMargin() - _doc.bottomMargin();

        XPathExpression exprRepParagraph = xpath.compile("reportDefinition/report/items/*");
        NodeList repChilds = (NodeList) exprRepParagraph.evaluate(xmlDoc, XPathConstants.NODESET);

        parseDocument(repChilds, pGetter);
    }

    public void LoadMarkup(String xmlMarkup, HashMap<String, byte[]> pFontBodies, PropertyGetter pGetter, Document _doc) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, DocumentException, ParseException {
        fontBodies = pFontBodies;

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlMarkup));
        org.w3c.dom.Document xmlDoc = db.parse(is);

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(new SkeletonNameSpaceContext());

        LoadFonts(xpath, xmlDoc);

        XPathExpression expr = xpath.compile("reportDefinition/report");
        org.w3c.dom.Node reportNode = (org.w3c.dom.Node) expr.evaluate(xmlDoc, XPathConstants.NODE);

        NamedNodeMap attrs = reportNode.getAttributes();

        initAttrs(attrs);

        setPageSize(pageSize, pageOrientation);
        _doc.setMargins(marginLeft, marginRight, marginTop, marginBottom);
        spaceLeft  = _doc.getPageSize().getHeight() - _doc.topMargin() - _doc.bottomMargin();

        XPathExpression exprRepParagraph = xpath.compile("reportDefinition/report/items/*");
        NodeList repChilds = (NodeList) exprRepParagraph.evaluate(xmlDoc, XPathConstants.NODESET);

        parseDocument(repChilds, pGetter);
    }

    public void initAttrs(NamedNodeMap attrs) {
        marginLeft = Float.parseFloat(parseAttribute(attrs, marginleft, margin_value));
        marginTop = Float.parseFloat(parseAttribute(attrs, margintop, margin_value));
        marginRight = Float.parseFloat(parseAttribute(attrs, marginright, margin_value));
        marginBottom = Float.parseFloat(parseAttribute(attrs, marginbottom, margin_value));

        pageFontName = parseAttribute(attrs,Report.pagefont, "arial");
        repPageNumHPos = parseAttribute(attrs, Report.pagenumhpos, "center");
        repPageNumVPos = parseAttribute(attrs, Report.pagenumvpos, "bottom");
        pageFontWeight = Integer.parseInt(parseAttribute(attrs, pagefontweight, "10"));
        pageNumType = parseAttribute(attrs, Report.pagenumtype, "blank");
        pageHeader = parseAttribute(attrs, Report.pageheader, "disabled");
        pageText = parseAttribute(attrs, Report.pagetext, "");
        lowerRunningTitle = parseAttribute(attrs,Report.lowerrunningtitle, "disabled");

        pageSize = parseAttribute(attrs, pagesize, A4);
        pageOrientation = parseAttribute(attrs, orientation, portrait);
    }

    private void setPageSize(String pageSize, String orientation) {

        if (pageSize.equalsIgnoreCase(A4)) {
            _doc.setPageSize(PageSize.A4);
        }

        if (pageSize.equalsIgnoreCase(A3)) {
            _doc.setPageSize(PageSize.A3);
        }

        if (pageSize.equalsIgnoreCase(A2)) {
            _doc.setPageSize(PageSize.A2);
        }

        if (pageSize.equalsIgnoreCase(A1)) {
            _doc.setPageSize(PageSize.A1);
        }

        if (pageSize.equalsIgnoreCase(A0)) {
            _doc.setPageSize(PageSize.A0);
        }

        if (pageSize.equalsIgnoreCase(B0)) {
            _doc.setPageSize(PageSize.B0);
        }

        if (pageSize.equalsIgnoreCase(B1)) {
            _doc.setPageSize(PageSize.B1);
        }

        if (pageSize.equalsIgnoreCase(B2)) {
            _doc.setPageSize(PageSize.B2);
        }

        if (pageSize.equalsIgnoreCase(B3)) {
            _doc.setPageSize(PageSize.B3);
        }

        if (pageSize.equalsIgnoreCase(B4)) {
            _doc.setPageSize(PageSize.B4);
        }

        if (orientation.equalsIgnoreCase(landscape)) {
            _doc.setPageSize(_doc.getPageSize().rotate());
        }
    }

    private void parseDocument(NodeList repChilds, PropertyGetter pGetter) throws ParseException, IOException, DocumentException {
        for (int t = 0; t < repChilds.getLength(); t++) {
            String nodeName = repChilds.item(t).getNodeName();
            if (nodeName.equals(lowerrunningtitle)) {
                NodeList lowerRunningTitleChildList = repChilds.item(t).getChildNodes();
            }

            if (nodeName.equals(header)){
                NodeList headerChildList = repChilds.item(t).getChildNodes();
                parseHeader(headerChildList, pGetter);
            }

            if (nodeName.equals(repeatingtemplate)){
                items.add(new ReportRepeatingTemplate(repChilds.item(t), fonts, null, pGetter, this));
            }

            if (nodeName.equals(paragraph)) {
                items.add(new ReportParagraph(repChilds.item(t), fonts, null, pGetter));
            }
            if (nodeName.equals(table)) {
                items.add(new ReportTable(repChilds.item(t), fonts, null, pGetter,this));
            }
            if (nodeName.equals(repeatingrow)) {
                items.add(new ReportRepeatingRow(repChilds.item(t), fonts, null, pGetter, _doc));
            }
            if (nodeName.equals(dateparagraph)) {
                items.add(new ReportDate(repChilds.item(t), fonts, null, pGetter));
            }
            if (nodeName.equals(wordsplitter)) {
                items.add(new ReportWordSplitter(repChilds.item(t), fonts, null, pGetter));
            }
            if (nodeName.equals(newpage)) {
                items.add(new ReportNewPage());
                curPage++;
            }
            if (nodeName.equals(ifcondition)) {
                NodeList ifStatementChildren = repChilds.item(t).getChildNodes();
                ReportConditionalStatements.parseIfStatement(ifStatementChildren, pGetter, logicalcondition, elsecondition, paragraph, items, fonts);
            }
        }
    }

    public static String parseAttribute(NamedNodeMap attrs, String attrName, String defaultValue) {
        if (attrs == null) return defaultValue;
        Node attrObj = attrs.getNamedItem(attrName);
        if (attrObj == null) return defaultValue;
        return attrObj.getTextContent();
    }

    private void LoadFonts(XPath xpath, org.w3c.dom.Document xmlDoc) throws XPathExpressionException, IOException, DocumentException {
        XPathExpression expr = xpath.compile("reportDefinition/fonts/baseFont");
        NodeList result = (NodeList) expr.evaluate(xmlDoc, XPathConstants.NODESET);

        for (int i = 0; i < result.getLength(); i++) {
            String fontName = result.item(i).getAttributes().getNamedItem(name).getTextContent();
            String fontPath = result.item(i).getAttributes().getNamedItem(path).getTextContent();
            ReportBaseFont baseFont = new ReportBaseFont(fontName, fontPath, fontBodies.get(fontName));
            fonts.put(fontName, baseFont);
        }
    }

    public byte[] GetDocument() throws DocumentException, ParseException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter wr = PdfWriter.getInstance(_doc, byteArrayOutputStream);
        wr.setPageEvent(new PageIncrementEvent(this));
        wr.setRgbTransparencyBlending(true);

        if(pageHeader.equalsIgnoreCase("enabled")){
            Font pageFont = new Font(fonts.get(pageFontName).getCustomFont(pageFontWeight));
            HeaderEvent event = new HeaderEvent(this, _doc, pageFont);
            wr.setPageEvent(event);
        }

        _doc.open();

        for (BaseReportObject item : items) {

            if (item instanceof ReportNewPage) {
                _doc.newPage();
                spaceLeft  = _doc.getPageSize().getHeight() - _doc.topMargin() - _doc.bottomMargin();
            }

            else if (item instanceof ReportRepeatingRow) {
                for(Object reportRepeatingRowItem : ((ReportRepeatingRow) item).getPdfTable(spaceLeft, _doc))
                {
                    if(reportRepeatingRowItem==null){
                        _doc.newPage();
                        spaceLeft  = _doc.getPageSize().getHeight() - _doc.topMargin() - _doc.bottomMargin();
                    }
                    else if (reportRepeatingRowItem instanceof PdfPTable){
                        ReportTableUtils.setExactWidthFromPercentage((PdfPTable) reportRepeatingRowItem, _doc);
                        spaceLeft= ReportDocumentUtils.calcFreeSpace(ReportTableUtils.getTableVerticalSize((PdfPTable) reportRepeatingRowItem), (Float) spaceLeft, _doc);
                        _doc.add((Element) reportRepeatingRowItem);
                    }
                }
            }

            else if (item instanceof ReportTable) {
                PdfPTable table = (PdfPTable) item.getPdfObject();
                ReportTableUtils.setExactWidthFromPercentage(table, _doc);
                spaceLeft= ReportDocumentUtils.calcFreeSpace(ReportTableUtils.getTableVerticalSize(table), (Float) spaceLeft, _doc);
                _doc.add(table);
            }
            else if (item instanceof ReportParagraph) {
                spaceLeft = ReportDocumentUtils.calcFreeSpace(item.getVerticalSize(), (Float) spaceLeft, _doc);
                _doc.add(item.getPdfObject());
            }
            else if(item instanceof ReportRepeatingTemplate){
                for(Element reportRepeatingTemplateItem : item.itemsGPO){
                    _doc.add(reportRepeatingTemplateItem);
                }
            }

            else if (item.getPdfObject() != null) _doc.add(item.getPdfObject());
        }

        PdfAction ac = PdfAction.gotoLocalPage(1, new
                PdfDestination(PdfDestination.XYZ, 0, _doc.getPageSize().getHeight(), 1f), wr);

        wr.setOpenAction(ac);
        _doc.close();
        wr.close();
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }


    public ArrayList GetDocumentF() throws DocumentException, ParseException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter wr = PdfWriter.getInstance(_doc, byteArrayOutputStream);
        wr.setPageEvent(new PageIncrementEvent(this));
        wr.setRgbTransparencyBlending(true);

        if(pageHeader.equalsIgnoreCase("enabled")){
            Font pageFont = new Font(fonts.get(pageFontName).getCustomFont(pageFontWeight));
            HeaderEvent event = new HeaderEvent(this, _doc, pageFont);
            wr.setPageEvent(event);
        }

        _doc.open();

        for (BaseReportObject item : items) {

            if (item instanceof ReportNewPage) {
                _doc.newPage();
                spaceLeft  = _doc.getPageSize().getHeight() - _doc.topMargin() - _doc.bottomMargin();
            }

            else if (item instanceof ReportRepeatingRow) {
                for(Object reportRepeatingRowItem : ((ReportRepeatingRow) item).getPdfTable(spaceLeft, _doc))
                {
                    if(reportRepeatingRowItem==null){
                        _doc.newPage();
                        spaceLeft  = _doc.getPageSize().getHeight() - _doc.topMargin() - _doc.bottomMargin();
                    }
                    else if (reportRepeatingRowItem instanceof PdfPTable){
                        ReportTableUtils.setExactWidthFromPercentage((PdfPTable) reportRepeatingRowItem, _doc);
                        spaceLeft= ReportDocumentUtils.calcFreeSpace(ReportTableUtils.getTableVerticalSize((PdfPTable) reportRepeatingRowItem), (Float) spaceLeft, _doc);
                        _doc.add((Element) reportRepeatingRowItem);
                    }
                }
            }

            else if (item instanceof ReportTable) {
                PdfPTable table = (PdfPTable) item.getPdfObject();
                ReportTableUtils.setExactWidthFromPercentage(table, _doc);
                spaceLeft= ReportDocumentUtils.calcFreeSpace(ReportTableUtils.getTableVerticalSize(table), (Float) spaceLeft, _doc);
                _doc.add(table);
            }
            else if (item instanceof ReportParagraph) {
                spaceLeft = ReportDocumentUtils.calcFreeSpace(item.getVerticalSize(), (Float) spaceLeft, _doc);
                _doc.add(item.getPdfObject());
            }
            else if(item instanceof ReportRepeatingTemplate){
                for(Element reportRepeatingTemplateItem : item.itemsGPO){
                    _doc.add(reportRepeatingTemplateItem);
                }
            }

            else if (item.getPdfObject() != null) _doc.add(item.getPdfObject());
        }

        PdfAction ac = PdfAction.gotoLocalPage(1, new
                PdfDestination(PdfDestination.XYZ, 0, _doc.getPageSize().getHeight(), 1f), wr);


        wr.setOpenAction(ac);
        items.clear();

        setCurPage(getCurPage()+1);
        ArrayList<Object> list = new ArrayList<>();
        list.add(byteArrayOutputStream);
        list.add(_doc);
        list.add(wr);

        return list;
    }

    public ArrayList GetDocumentM(Document _doc, ByteArrayOutputStream byteArrayOutputStream, PdfWriter wr) throws DocumentException, ParseException, IOException {

        wr.setPageEvent(new PageIncrementEvent(this));
        wr.setRgbTransparencyBlending(true);


        for (BaseReportObject item : items) {

            if (item instanceof ReportNewPage) {
                _doc.newPage();
                spaceLeft  = _doc.getPageSize().getHeight() - _doc.topMargin() - _doc.bottomMargin();
            }

            else if (item instanceof ReportRepeatingRow) {
                for(Object reportRepeatingRowItem : ((ReportRepeatingRow) item).getPdfTable(spaceLeft, _doc))
                {
                    if(reportRepeatingRowItem==null){
                        _doc.newPage();
                        spaceLeft  = _doc.getPageSize().getHeight() - _doc.topMargin() - _doc.bottomMargin();
                    }
                    else if (reportRepeatingRowItem instanceof PdfPTable){
                        ReportTableUtils.setExactWidthFromPercentage((PdfPTable) reportRepeatingRowItem, _doc);
                        spaceLeft= ReportDocumentUtils.calcFreeSpace(ReportTableUtils.getTableVerticalSize((PdfPTable) reportRepeatingRowItem), (Float) spaceLeft, _doc);
                        _doc.add((Element) reportRepeatingRowItem);
                    }
                }
            }

            else if (item instanceof ReportTable) {
                PdfPTable table = (PdfPTable) item.getPdfObject();
                ReportTableUtils.setExactWidthFromPercentage(table, _doc);
                spaceLeft= ReportDocumentUtils.calcFreeSpace(ReportTableUtils.getTableVerticalSize(table), (Float) spaceLeft, _doc);
                _doc.add(table);
            }
            else if (item instanceof ReportParagraph) {
                spaceLeft = ReportDocumentUtils.calcFreeSpace(item.getVerticalSize(), (Float) spaceLeft, _doc);
                _doc.add(item.getPdfObject());
            }

            else if(item instanceof ReportRepeatingTemplate){
                for(Element reportRepeatingTemplateItem : item.itemsGPO){
                    _doc.add(reportRepeatingTemplateItem);
                }
            }

            else if (item.getPdfObject() != null) _doc.add(item.getPdfObject());
        }

        items.clear();

        setCurPage(getCurPage()+1);
        ArrayList<Object> list = new ArrayList<>();
        list.add(byteArrayOutputStream);
        list.add(_doc);
        list.add(wr);

        return list;
    }

    public byte[] GetDocumentE(Document _doc, ByteArrayOutputStream byteArrayOutputStream, PdfWriter wr) throws DocumentException, ParseException, IOException {

        wr.setPageEvent(new PageIncrementEvent(this));
        wr.setRgbTransparencyBlending(true);


        for (BaseReportObject item : items) {

            if (item instanceof ReportNewPage) {
                _doc.newPage();
                spaceLeft  = _doc.getPageSize().getHeight() - _doc.topMargin() - _doc.bottomMargin();
            }

            else if (item instanceof ReportRepeatingRow) {
                for(Object reportRepeatingRowItem : ((ReportRepeatingRow) item).getPdfTable(spaceLeft, _doc))
                {
                    if(reportRepeatingRowItem==null){
                        _doc.newPage();
                        spaceLeft  = _doc.getPageSize().getHeight() - _doc.topMargin() - _doc.bottomMargin();
                    }
                    else if (reportRepeatingRowItem instanceof PdfPTable){
                        ReportTableUtils.setExactWidthFromPercentage((PdfPTable) reportRepeatingRowItem, _doc);
                        spaceLeft= ReportDocumentUtils.calcFreeSpace(ReportTableUtils.getTableVerticalSize((PdfPTable) reportRepeatingRowItem), (Float) spaceLeft, _doc);
                        _doc.add((Element) reportRepeatingRowItem);
                    }
                }
            }

            else if (item instanceof ReportTable) {
                PdfPTable table = (PdfPTable) item.getPdfObject();
                ReportTableUtils.setExactWidthFromPercentage(table, _doc);
                spaceLeft= ReportDocumentUtils.calcFreeSpace(ReportTableUtils.getTableVerticalSize(table), (Float) spaceLeft, _doc);
                _doc.add(table);
            }
            else if (item instanceof ReportParagraph) {
                spaceLeft = ReportDocumentUtils.calcFreeSpace(item.getVerticalSize(), (Float) spaceLeft, _doc);
                _doc.add(item.getPdfObject());
            }

            else if(item instanceof ReportRepeatingTemplate){
                for(Element reportRepeatingTemplateItem : item.itemsGPO){
                    _doc.add(reportRepeatingTemplateItem);
                }
            }

            else if (item.getPdfObject() != null) _doc.add(item.getPdfObject());
        }

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

    protected void parseHeader(NodeList headerChildList, PropertyGetter pGetter) throws IOException, DocumentException {
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



 /*   protected void setPageNumber(PdfWriter writer){
        //this method is obsolete, but still can be useful
        float verticalPosition;
        BaseFont pageBF = fonts.get(pageFontName).getBaseFont();

        if(repPageNumVPos.equalsIgnoreCase("top")){
            verticalPosition=0.96f;
        } else verticalPosition=0.05f;

        if( (!repPageNumHPos.equalsIgnoreCase("blank") && curPage>1) || (!repPageNumHPos.equalsIgnoreCase("blank") && curPage>1) ){
            PdfContentByte cb = writer.getDirectContent();
            cb.saveState();
            cb.beginText();
            if(repPageNumHPos.equalsIgnoreCase("right")) cb.moveText((float) (_doc.getPageSize().getWidth()*0.96 - _doc.rightMargin() ), (float) (_doc.getPageSize().getHeight()*verticalPosition));
            if(repPageNumHPos.equalsIgnoreCase("center")) cb.moveText((float) (_doc.getPageSize().getWidth()*0.5 ), (float) (_doc.getPageSize().getHeight()*verticalPosition));
            if(repPageNumHPos.equalsIgnoreCase("left")) cb.moveText((float) (_doc.getPageSize().getWidth()*0.05 + _doc.leftMargin()), (float) (_doc.getPageSize().getHeight()*verticalPosition));
            cb.setFontAndSize(pageBF, pageFontWeight);
            cb.showText("Лист " + curPage);
            curPage++;
            cb.endText();
            cb.restoreState();
        } else if (curPage==1) curPage++;
    }*/
}