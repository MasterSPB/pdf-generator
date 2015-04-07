package ru.onyx.clipper.pdfgenerator.model;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import org.w3c.dom.Node;
import ru.onyx.clipper.pdfgenerator.data.PropertyGetter;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Tag for formatting day of date. For use only with ReportDate and inside <date> tag
 * User: Alex
 * Date: 20.03.12
 * Time: 15:53
 */
public class ReportDateDay extends BaseReportObject {

    public ReportDateDay(Node node, HashMap<String, ReportBaseFont> fonts, BaseReportObject pParent, PropertyGetter pGetter) throws ParseException{
        _fonts = fonts;
        parent = pParent;
        propertyGetter = pGetter;
        Load(node);
    }

    @Override
    public Element getPdfObject() throws DocumentException, ParseException {

        Date s = propertyGetter.GetPropertyStringAsDate(getPropertyName(),getDateFormat());
        if (s != null) {
                Calendar ca1 = Calendar.getInstance();
                ca1.setTime(s);
                int iDay = ca1.get(Calendar.DAY_OF_MONTH);

                return new Chunk(String.valueOf(iDay), getFont());
           }
        return new Chunk("");
    }
}
