package ru.onyx.clipper.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by anton on 29.04.14.
 * The input number decimal separator should be a dot
 */
public class ReportStrUtils {

	/**
	 * Заменяет десятчиный разделитель '.' на указанный и добавляет символы '00' к строке.
	 * @param inputString число с десятичным разделителем '.'
	 * @param separator новый символ разделителя.
	 * @return число с новым десятичным разделителем.
	 */
    public static String replaceDecSeparator(String inputString, String separator){
        if(inputString!=null) {
			if(inputString.equals("")) {
				return inputString;
			}
			else{
				DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
				symbols.setDecimalSeparator(separator.charAt(0));
				DecimalFormat df = new DecimalFormat("#.00", symbols);
				return df.format(Double.parseDouble(inputString));
			}
        }
        return null;
    }

	/**
	 * Обрамляет отрицательное значение скобками. Знак '-' при этом не выводится.
	 * @param inputString отрицательное значение со знаком '-' (строкове)
	 * @return модуль отрицательного значения в скобках (строковое) или само значение, если оно положительно
	 */
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
