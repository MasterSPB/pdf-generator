package ru.onyx.clipper.utils;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static String getFormattedDate(String defaultNullValue, Date s, String format){
   // returns formatted date or default value if null is passed instead of date

        if (s == null) return defaultNullValue;

        DateFormat df;

        String[] russianMonth = {
                "января",
                "февраля",
                "марта",
                "апреля",
                "мая",
                "июня",
                "июля",
                "августа",
                "сентября",
                "октября",
                "ноября",
                "декабря"
        };

        if(format.toLowerCase().contains("::r")) {
            format = format.substring(0, format.indexOf("::R"));
            DateFormatSymbols russSymbol = new DateFormatSymbols();
            russSymbol.setMonths(russianMonth);
            df = new SimpleDateFormat(format,russSymbol);
        }
        else {
            df = new SimpleDateFormat(format);
        }

        return df.format(s);
    }
}