package ru.onyx.clipper.model;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import org.w3c.dom.Node;
import ru.onyx.clipper.data.PropertyGetter;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * User: Alex
 * Date: 20.03.12
 * Time: 16:04
 */
public class ReportDateYear extends BaseReportObject {

    public ReportDateYear(Node node, HashMap<String, ReportBaseFont> fonts, BaseReportObject pParent, PropertyGetter pGetter) {
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
            int iYear = ca1.get(Calendar.YEAR);
            String year = String.valueOf(iYear);
            if (getLastSymbolsNum() >= 0) {
                int len = year.length();
                int startindex = len - getLastSymbolsNum();
                year = year.substring(startindex);
            }

            return new Chunk(year, getFont());
        }
        return new Chunk("");
    }
}

