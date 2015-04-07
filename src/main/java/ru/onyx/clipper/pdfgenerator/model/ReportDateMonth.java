package ru.onyx.clipper.pdfgenerator.model;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import org.w3c.dom.Node;
import ru.onyx.clipper.pdfgenerator.data.PropertyGetter;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Tag for formatting month. For use only in ReportDate and inside <date> tag
 * Date: 20.03.12
 * Time: 15:59
 */
public class ReportDateMonth extends BaseReportObject {

    ArrayList<String> _montsRu = new ArrayList<String>();

    public ReportDateMonth(Node node, HashMap<String, ReportBaseFont> fonts, BaseReportObject pParent, PropertyGetter pGetter) throws ParseException{
        _fonts = fonts;
        parent = pParent;
        propertyGetter = pGetter;
        Load(node);

    }

    @Override
    public Element getPdfObject() throws DocumentException, ParseException {

        _montsRu.add("Января");
        _montsRu.add("Февраля");
        _montsRu.add("Марта");
        _montsRu.add("Апреля");
        _montsRu.add("Мая");
        _montsRu.add("Июня");
        _montsRu.add("Июля");
        _montsRu.add("Августа");
        _montsRu.add("Сентября");
        _montsRu.add("Октября");
        _montsRu.add("Ноября");
        _montsRu.add("Декабря");

       Date s = propertyGetter.GetPropertyStringAsDate(getPropertyName(),getDateFormat());
        if (s != null) {

                Calendar ca1 = Calendar.getInstance();
                ca1.setTime(s);
                DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
                //Get the short names for months in a Calendar
                String[] months;
                String iMonth = "";
                if (getMonthFormat() == 1) {
                    months = dateFormatSymbols.getShortMonths();
                    iMonth = months[ca1.get(Calendar.MONTH)];
                }
                if (getMonthFormat() == 2) {
                    months = dateFormatSymbols.getMonths();
                    iMonth = _montsRu.get(ca1.get(Calendar.MONTH));
                }
                if (getMonthFormat() == 3) {
                    iMonth = String.valueOf(ca1.get(Calendar.MONTH) + 1);
                    if (iMonth.length() == 1) iMonth = "0" + iMonth;
                }

                return new Chunk(iMonth.toLowerCase(), getFont());
        }
        return new Chunk("");
    }
}

