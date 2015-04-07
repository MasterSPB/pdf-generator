package ru.onyx.clipper.pdfgenerator.model;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.text.ParseException;

/**
 * User: ViktorZahar
 * Date: 25.03.2015
 * Add new report section. Allow define pageSize, pageMargin and pageOrientation
 */
public class ReportNewSection extends BaseReportObject {

    public ReportNewSection(Node node) throws ParseException {
        Load(node);
    }

    @Override
    public Element getPdfObject() throws DocumentException, ParseException, IOException {
        return null;
    }
}
