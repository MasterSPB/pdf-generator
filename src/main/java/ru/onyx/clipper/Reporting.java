package ru.onyx.clipper;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import org.xml.sax.SAXException;
import ru.onyx.clipper.data.PropertyGetter;
import ru.onyx.clipper.model.Report;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: MasterSPB
 * Date: 15.03.12
 * Time: 22:20
 */
public class Reporting {

     public Reporting() {

     }

    public static byte[] CreateDocumentEx(String markup, HashMap<String, byte[]> fonts, PropertyGetter dataSource) throws IOException, DocumentException, SAXException, XPathExpressionException, ParserConfigurationException, ParseException {

        Report rep = new Report();

        rep.LoadMarkup(markup, fonts, dataSource);
        return rep.GetDocument();
    }

    public static byte[] CreateDocumentEx(ArrayList<String> markup, HashMap<String, byte[]> fonts, PropertyGetter dataSource[]) throws IOException, DocumentException, SAXException, XPathExpressionException, ParserConfigurationException, ParseException {

        Report rep = new Report();

        if(markup.size() == 2){

            rep.LoadMarkup(markup.get(0), fonts, dataSource[0]);
            ArrayList docTemp = rep.GetDocumentF();
            rep.LoadMarkup(markup.get(1),fonts, dataSource[1],(Document)docTemp.get(1));

            return rep.GetDocumentE((Document)docTemp.get(1),(ByteArrayOutputStream)docTemp.get(0),(PdfWriter)docTemp.get(2));

        }else{
            rep.LoadMarkup(markup.get(0), fonts, dataSource[0]);
            ArrayList docTemp = rep.GetDocumentF();
            for(int m=1; m < markup.size()-1; m++){
                rep.LoadMarkup(markup.get(m),fonts, dataSource[m],(Document)docTemp.get(1));
                docTemp = rep.GetDocumentM((Document)docTemp.get(1),(ByteArrayOutputStream)docTemp.get(0),(PdfWriter)docTemp.get(2));
            }

            rep.LoadMarkup(markup.get(markup.size()-1),fonts, dataSource[dataSource.length-1],(Document)docTemp.get(1));

            return rep.GetDocumentE((Document)docTemp.get(1),(ByteArrayOutputStream)docTemp.get(0),(PdfWriter)docTemp.get(2));
        }

    }

    public static void writeDocument(String pdfPath, Object rep) {
        File f = new File(pdfPath);
        FileOutputStream file = null;

        try {
            file = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            file.write((byte[]) rep);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] CreateDocument(String markup, String fontNames, PropertyGetter dataSource) {
        try {
            HashMap<String, byte[]> fonts = FontHelper.GetFonts(fontNames);
            return CreateDocumentEx(markup, fonts, dataSource);
        } catch (Exception e) {
            throw new RuntimeException("Can't Create Document ", e);
        }
    }



    public byte[] ConcatenateDocuments(String pageName,String propertyName,PropertyGetter propGetter) throws IOException, DocumentException {
        Document concatDoc = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // step 2
        PdfCopy copy = new PdfCopy(concatDoc, out);

        // step 3
        concatDoc.open();
        // step 4
        PdfReader reader;
        int n;
        int pageSize =  propGetter.GetPageCount(pageName);

        // loop over the documents you want to concatenate
        if(pageSize>0) {
            for (int i = 0; i < pageSize; i++) {
                byte[] doc = propGetter.GetProperty(propGetter.GetProperty(String.format("%s(%s).%s", pageName, i + 1, propertyName))).getBytes();
                reader = new PdfReader(doc);
                // loop over the pages in that document
                n = reader.getNumberOfPages();
                for (int page = 0; page < n; ) {
                    copy.addPage(copy.getImportedPage(reader, ++page));
                }
                copy.freeReader(reader);
            }
        }

        return out.toByteArray();
    }
}
