package ru.onyx.clipper.model;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import org.w3c.dom.Node;
import ru.onyx.clipper.data.PropertyGetter;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

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
