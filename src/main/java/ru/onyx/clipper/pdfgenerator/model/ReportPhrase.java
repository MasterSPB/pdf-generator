package ru.onyx.clipper.pdfgenerator.model;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import org.w3c.dom.Node;
import ru.onyx.clipper.pdfgenerator.data.PropertyGetter;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

/**
 * User: Alex
 * Date: 14.03.12
 * Time: 17:43
 */
public class ReportPhrase extends BaseReportObject {

    public ReportPhrase(Node node, HashMap<String, ReportBaseFont> fonts, BaseReportObject pParent, PropertyGetter pGetter, Report report) throws ParseException, IOException, DocumentException {

        _fonts = fonts;
        parent = pParent;
        propertyGetter = pGetter;
        this.report=report;
        Load(node);
        LoadItems(node, fonts, this, pGetter);

    }


    @Override
    public Element getPdfObject() throws DocumentException, ParseException, IOException {
        Phrase ph = new Phrase();
        if (this.leading > 0) ph.setLeading(this.leading);
        if (getFont() != null) ph.setFont(getFont());
        if (this.items.size() > 0) {
            for (BaseReportObject item : this.items) {
                ph.add(item.getPdfObject());
            }
        }
        return ph;
    }


}
