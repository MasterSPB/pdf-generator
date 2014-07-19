package ru.onyx.clipper.utils;

import java.util.regex.Pattern;

/**
 * Created by anovik on 15.07.14.
 */
public class ReportRegexUtils {

    public static Pattern getRegex(String regexp, String operand, String quartindex){
        Pattern p;
        Integer op = null;

        if(regexp.equals("date")){
            p=Pattern.compile(operand);
            return p;
        }else if(regexp.equals("dateDay")){
            p = Pattern.compile("....-..-"+operand);
            return p;
        }else if(regexp.equals("dateMonth")){
            if(!quartindex.equals(null)) {

                if((Integer.parseInt(quartindex))==1){
                    op=Integer.parseInt(operand);
                }else if((Integer.parseInt(quartindex))==2){
                    op=Integer.parseInt(operand)+3;
                }else if((Integer.parseInt(quartindex))==3){
                    op=Integer.parseInt(operand)+6;
                }else if((Integer.parseInt(quartindex))==4){
                    op=Integer.parseInt(operand)+9;
                }

                if(op<10){
                    p = Pattern.compile("....-" + "0" + op + "-..");
                    return p;
                }
                p = Pattern.compile("....-" + op + "-..");
                return p;
            }else{
                p = Pattern.compile("....-" + operand + "-..");
                return p;
            }
        }else if (regexp.equals("dateYear")){
            p = Pattern.compile(operand+"-..-..");
            return p;
        }else{
            return null;
        }
    }

}
