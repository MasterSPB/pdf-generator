package ru.onyx.clipper.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by anton on 29.04.14.
 * The input number decimal separator should be a dot
 */
public class ReportStrUtils {

    public static String replaceDecSeparator(String inputString, String separator){

        if(inputString.equals("")) return inputString;

        if(inputString!=null) {
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
            otherSymbols.setDecimalSeparator(separator.charAt(0));
            DecimalFormat df = new DecimalFormat("#.00", otherSymbols);
            return df.format(Float.parseFloat(inputString));
        }


        return null;
    }

    public static String embraceNegativeValue(String inputString){
        if ( inputString.matches("[0-9-]*") ) {
            if (Double.parseDouble(inputString) >= 0)
                return inputString;

            inputString = inputString.replace('-', '(');
            inputString = inputString+')';
        }
        return inputString;
    }
}
