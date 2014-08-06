package ru.onyx.clipper.data;

import com.itextpdf.text.Image;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import ru.onyx.clipper.data.PropertyGetter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: MasterSPB
 * Date: 15.03.12
 * Time: 23:40
 */
public class PropertyGetterFromXMLFileImpl implements PropertyGetter {
    private Document doc;

    public int GetPageCount(String pName) {
        try {
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            String xPathString = getXPathCount(pName);

            XPathExpression xPathExpr = xpath.compile(xPathString);
            return Integer.parseInt(xPathExpr.evaluate(doc));
        } catch (NullPointerException e) {
            e.printStackTrace();
            return 0;
        }catch (ClassCastException cce){
            return -1;
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public PropertyGetterFromXMLFileImpl(String reportDataXmlPath) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        DocumentBuilder db = factory.newDocumentBuilder();
        doc = db.parse(reportDataXmlPath);
    }

    public String GetProperty(String pName) {

        try {
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            String xPathString = getXPathString(pName);

            XPathExpression xPathExpr = xpath.compile(xPathString);
            return xPathExpr.evaluate(doc);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Date GetPropertyAsDate(String pName) {
        try {
            String dateAsString = GetProperty(pName);

            final String pattern = "yyyyMMdd'T'hhmmss";
            final SimpleDateFormat sdf = new SimpleDateFormat(pattern);


            return sdf.parse(dateAsString);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    /**
     * @param propvalue   - значение property
     * @param pDateFormat - формат даты
     * @return
     */
    public Date GetPropertyStringAsDate(String propvalue, String pDateFormat) {
        if (pDateFormat == null) return null;
        DateFormat df = new SimpleDateFormat(pDateFormat);
        if (propvalue == null) return null;
        if (propvalue.length() == 0) return null;
        try {
            return df.parse(propvalue);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getXPath(String pName) {
        String result = "clipboard";
        if (pName.startsWith("."))
            result += "/pagedata";
        else
            result += "/";
        if (pName.contains("("))
            pName = pName.replace("(", "/rowdata[@REPEATINGINDEX='");
        if (pName.contains(")"))
            pName = pName.replace(")", "']");
        result += pName.replace('.', '/');
        return result;
    }

    private String getXPathString(String pName) {
        return getXPath(pName) + "/text()";
    }

    private String getXPathCount(String pName) {
        return "count(" + getXPath(pName) + "/rowdata)";
    }

    public Image GetImage(String fileName, String folderName, String fileType) {
        Image img = null;
        try {
            String fullName = folderName + "\\" + fileName + "." + fileType;

            img = Image.getInstance(fullName);
            return img;
        } catch (Exception e) {
            return img;
        }
    }
}