package ru.onyx.clipper.pdfgenerator.model;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;

import java.io.IOException;
import java.text.ParseException;

/**
 * User: Alex
 * Date: 10.08.12
 * Time: 11:25
 */
public class ReportNewPage extends BaseReportObject {

    public ReportNewPage() throws ParseException {

    }

    @Override
    public Element getPdfObject() throws DocumentException, ParseException, IOException {
        return null;
    }
}
