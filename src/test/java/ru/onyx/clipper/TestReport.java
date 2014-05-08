package ru.onyx.clipper;

import com.itextpdf.text.DocumentException;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.SAXException;
import ru.onyx.clipper.data.PropertyGetter;
import ru.onyx.clipper.utils.ConversionUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
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
        String markup = getFileContent(new ClassPathResource("reports/report1/financial statement.xml").getFile().getAbsolutePath());

        HashMap<String, byte[]> fontBodies = new HashMap<String, byte[]>();
        fontBodies.put("arial", getFileFontByteContent("arial"));
        fontBodies.put("times", getFileFontByteContent("times"));
        fontBodies.put("arialbi", getFileFontByteContent("arialbi"));
        fontBodies.put("ariali", getFileFontByteContent("ariali"));
        fontBodies.put("arialbd", getFileFontByteContent("arialbd"));
        fontBodies.put("wingding", getFileFontByteContent("wingding"));


       // ConversionUtils.XMLtoJSON(new ClassPathResource("reports/report1/financialstatementdata.xml").getFile().getAbsolutePath(),new ClassPathResource("reports/report1/financialstatementdata.json").getFile().getAbsolutePath());

        PropertyGetter getterTest = new PropertyGetterTest2(new ClassPathResource("reports/report1/financialstatementdata.json").getFile().getPath());
        Object rep = Reporting.CreateDocumentEx(markup, fontBodies, getterTest);
        File f = new File("test_fin_statement.pdf");
        FileOutputStream file = new FileOutputStream(f);
        file.write((byte[]) rep);
    }
}