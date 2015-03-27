package ru.onyx.clipper;

import com.itextpdf.text.DocumentException;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.SAXException;
import ru.onyx.clipper.data.PropertyGetter;
import ru.onyx.clipper.data.PropertyGetterFromJSONFileImpl;
import ru.onyx.clipper.data.PropertyGetterFromJSONStringImpl;
import ru.onyx.clipper.utils.ReportConversionUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by anton on 03.04.14.
 */
public class TestReport {

    private static String getFileContent(String fileName) throws IOException {
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        byte[] fileContent = new byte[(int) file.length()];

        fis.read(fileContent);
        fis.close();

        return new String(fileContent);
    }

    private byte[] getFileFontByteContent(String name) throws IOException {

        File file = new ClassPathResource("fonts/" + name + ".ttf").getFile();
        FileInputStream inStream = new FileInputStream(file);
        byte fileContent[] = new byte[(int) file.length()];
        inStream.read(fileContent);
        return fileContent;
    }

    @Test
    public void test1() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, ParseException, DocumentException {
        //String markup = getFileContent(new ClassPathResource("reports/report1/payment order.xml").getFile().getAbsolutePath());
        String markup = getFileContent(new ClassPathResource("reports/report1/financial statement.xml").getFile().getAbsolutePath());


        HashMap<String, byte[]> fontBodies = new HashMap<String, byte[]>();
        fontBodies.put("arial", getFileFontByteContent("arial"));
        fontBodies.put("times", getFileFontByteContent("times"));
        fontBodies.put("arialbi", getFileFontByteContent("arialbi"));
        fontBodies.put("ariali", getFileFontByteContent("ariali"));
        fontBodies.put("arialbd", getFileFontByteContent("arialbd"));
        fontBodies.put("wingding", getFileFontByteContent("wingding"));


        Path path = Paths.get(new ClassPathResource("reports/report1/financialstatementdata.xml").getFile().getAbsolutePath());
        String jsonString = ReportConversionUtils.ByteXMLtoJSON(Files.readAllBytes(path),"windows-1251");

        //PropertyGetter getterTest = new PropertyGetterFromJSONFileImpl(new ClassPathResource("reports/report1/payment order.json").getFile().getPath());
        PropertyGetter getterTest = new PropertyGetterFromJSONStringImpl(jsonString);
        //PropertyGetter getterTest = new PropertyGetterFromJSONFileImpl(new ClassPathResource("reports/report1/invoice.json").getFile().getPath());
        Object rep = Reporting.CreateDocumentEx(markup, fontBodies, getterTest);
        //Reporting.writeDocument("/home/anton/docs/payment order.pdf", rep);
        Reporting.writeDocument("/home/anton/docs/bill.pdf", rep);
    }

    @Test
    public void makeJsonFromXml() throws  IOException {
        ReportConversionUtils.XMLtoJSON(new ClassPathResource("reports/report1/declarationBUHdata.xml").getFile().getAbsolutePath(),
                new ClassPathResource("reports/report1/declarationBUHdata.json").getFile().getAbsolutePath(),
                "UTF-8"
        );
    }


    @Test
    public void testPdf() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, ParseException, DocumentException {
        makePdf("declarationNDS_2015", "declarationNDS_2015");
        //makePdf("payment order", "payment order");
    }

    public void makePdf(String docName, String docDataName) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, ParseException, DocumentException {
        String markup = getFileContent(new ClassPathResource("reports/report1/" + docName + ".xml").getFile().getAbsolutePath());

        HashMap<String, byte[]> fontBodies = new HashMap<String, byte[]>();
        fontBodies.put("arial", getFileFontByteContent("arial"));
        fontBodies.put("times", getFileFontByteContent("times"));
        fontBodies.put("arialbi", getFileFontByteContent("arialbi"));
        fontBodies.put("ariali", getFileFontByteContent("ariali"));
        fontBodies.put("arialbd", getFileFontByteContent("arialbd"));
        fontBodies.put("wingding", getFileFontByteContent("wingding"));

        PropertyGetter getterTest = new PropertyGetterFromJSONFileImpl(new ClassPathResource("reports/report1/" + docDataName + ".json").getFile().getPath());
        Object rep = Reporting.CreateDocumentEx(markup, fontBodies, getterTest);
        Reporting.writeDocument("/home/user/generated_docs/" + docName + ".pdf", rep);
    }

    @Test
    public void test2() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, ParseException, DocumentException {
        String markup = getFileContent(new ClassPathResource("reports/report1/declarationBUHMP.xml").getFile().getAbsolutePath());
        //String markup = getFileContent(new ClassPathResource("reports/report1/financial statement.xml").getFile().getAbsolutePath());


        HashMap<String, byte[]> fontBodies = new HashMap<String, byte[]>();
        fontBodies.put("arial", getFileFontByteContent("arial"));
        fontBodies.put("times", getFileFontByteContent("times"));
        fontBodies.put("arialbi", getFileFontByteContent("arialbi"));
        fontBodies.put("ariali", getFileFontByteContent("ariali"));
        fontBodies.put("arialbd", getFileFontByteContent("arialbd"));
        fontBodies.put("wingding", getFileFontByteContent("wingding"));

//        ReportConversionUtils.XMLtoJSON(new ClassPathResource("reports/report1/FSS_actual_raw.xml").getFile().getAbsolutePath(), new ClassPathResource("reports/report1/FSS_actual.json").getFile().getAbsolutePath());

        PropertyGetter getterTest = new PropertyGetterFromJSONFileImpl(new ClassPathResource("reports/report1/DeclarationBUHMPdata.json").getFile().getPath());
        Object rep = Reporting.CreateDocumentEx(markup, fontBodies, getterTest);
        Reporting.writeDocument("/home/anton/generated_docs/declarationBUHMP.pdf", rep);
    }

    @Test
    public void test3() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, ParseException, DocumentException {
        String markup1 = getFileContent(new ClassPathResource("reports/report1/declarationRSV.xml").getFile().getAbsolutePath());
        String markup2 = getFileContent(new ClassPathResource("reports/report1/declarationRSV2.xml").getFile().getAbsolutePath());
        ArrayList<String> markups = new ArrayList<>();
        markups.add(0,markup1);
        markups.add(1,markup2);
        //String markup = getFileContent(new ClassPathResource("reports/report1/financial statement.xml").getFile().getAbsolutePath());


        HashMap<String, byte[]> fontBodies = new HashMap<String, byte[]>();
        fontBodies.put("arial", getFileFontByteContent("arial"));
        fontBodies.put("times", getFileFontByteContent("times"));
        fontBodies.put("arialbi", getFileFontByteContent("arialbi"));
        fontBodies.put("ariali", getFileFontByteContent("ariali"));
        fontBodies.put("arialbd", getFileFontByteContent("arialbd"));
        fontBodies.put("wingding", getFileFontByteContent("wingding"));

//        ReportConversionUtils.XMLtoJSON(new ClassPathResource("reports/report1/rsv2.xml").getFile().getAbsolutePath(), new ClassPathResource("reports/report1/declarationRSV2.json").getFile().getAbsolutePath());

        PropertyGetter getterTest1 = new PropertyGetterFromJSONFileImpl(new ClassPathResource("reports/report1/declarationRSV.json").getFile().getPath());
        PropertyGetter getterTest2 = new PropertyGetterFromJSONFileImpl(new ClassPathResource("reports/report1/declarationRSV2.json").getFile().getPath());
        PropertyGetter getterTests[] = new PropertyGetter[2];
        getterTests[0]= getterTest1;
        getterTests[1] = getterTest2;
        Object rep = Reporting.CreateDocumentEx(markups, fontBodies, getterTests);
        Reporting.writeDocument("/home/anton/generated_docs/declarationRSVBoth.pdf", rep);
//        Reporting.writeDocument("/home/user/pdf_documents_gen/declarationRSV.pdf", rep);
        //Reporting.writeDocument("/home/anton/docs/financialstatement.pdf", rep);
    }
}