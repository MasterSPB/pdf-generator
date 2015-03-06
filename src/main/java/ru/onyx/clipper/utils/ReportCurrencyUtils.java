package ru.onyx.clipper.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by anton on 28.04.14.
 * Util file for currency operations
 */

    public class ReportCurrencyUtils {

        private BigDecimal amount;

        public ReportCurrencyUtils(long l) {
            String strValue = String.valueOf(l);
            if (!strValue.contains(".") )
                strValue += ".0";
            this.amount = new BigDecimal( strValue );
        }


        public ReportCurrencyUtils(double l) {
            String strValue = String.valueOf(l);
            if (!strValue.contains(".") )
                strValue += ".0";
            this.amount = new BigDecimal( strValue );
        }


        public ReportCurrencyUtils(String strValue) {
            if (!strValue.contains(".") )
                strValue += ".0";
            this.amount = new BigDecimal( strValue );
        }


        public String asString() {
            return amount.toString();
        }


        public String num2str() {
            return num2str(false);
        }

        /**
         * Output of sum
         * @param stripkop boolean - does it need to strip kops or not
         *
         */
        public String num2str(boolean stripkop) {
            // round to two decimal places
            amount = amount.setScale(2, RoundingMode.HALF_UP);

            String[][] kind = {
                    {"","один","два","три","четыре","пять","шесть","семь","восемь","девять"},
                    {"","одна","две","три","четыре","пять","шесть","семь","восемь","девять"},
            };
            String[] strHundreds= {"","сто","двести","триста","четыреста","пятьсот","шестьсот","семьсот", "восемьсот","девятьсот"};
            String[] strTeens = {"","десять","одиннадцать","двенадцать","тринадцать","четырнадцать", "пятнадцать","шестнадцать","семнадцать","восемнадцать","девятнадцать","двадцать"};
            String[] strTens = {"","десять","двадцать","тридцать","сорок","пятьдесят","шестьдесят", "семьдесят","восемьдесят","девяносто"};
            String[][] forms = {
                    {"копейка", "копейки", "копеек", "1"},
                    {"рубль", "рубля", "рублей", "0"},
                    {"тысяча", "тысячи", "тысяч", "1"},
                    {"миллион", "миллиона", "миллионов", "0"},
                    {"миллиард","миллиарда","миллиардов","0"},
                    {"триллион","триллиона","триллионов","0"},
            };
            // get rubles and kops
            long rub = amount.longValue();
            String[] moi = amount.toString().split("\\.");

            long kop = Long.valueOf(moi[1]);
            if (!moi[1].substring( 0,1).equals("0") ){// begins not from zero
                if (kop<10 )
                    kop *=10;
            }
            String kops = String.valueOf(kop);
            if (kops.length()==1 )
                kops = "0"+kops;
            long rub_tmp = rub;
            // cuts the sum into triads beginning from the end
            ArrayList segments = new ArrayList();
            while(rub_tmp>999) {
                long seg = rub_tmp/1000;
                segments.add( rub_tmp-(seg*1000) );
                rub_tmp=seg;
            }
            segments.add( rub_tmp );
            Collections.reverse(segments);
            // segment analyze
            String o = "";
            if (rub== 0) {// if zero
                o = "ноль "+morph( 0, forms[1][ 0],forms[1][1],forms[1][2]);
                if (stripkop)
                    return o;
                else
                    return o +" "+kop+" "+morph(kop,forms[ 0][ 0],forms[ 0][1],forms[ 0][2]);
            }
            // positive
            int level = segments.size();
            for (int i= 0; i<segments.size(); i++ ) {// segment iterator
                int sexi = Integer.valueOf( forms[level][3].toString() );// get sex
                int currentSegment = Integer.valueOf( segments.get(i).toString() );
                if (currentSegment == 0 && level>1) {
                    level--;
                    continue;
                }
                String rs = String.valueOf(currentSegment);
                // нормализация
                if (rs.length()==1) rs = "00"+rs;// adds two or
                if (rs.length()==2) rs = "0"+rs; // one zeroes to prefix

                int firstDigit = Integer.valueOf( rs.substring( 0,1) );
                int secondDigit = Integer.valueOf( rs.substring(1,2) );
                int thirdDigit = Integer.valueOf( rs.substring(2,3) );
                int secondAndThirdDigit= Integer.valueOf( rs.substring(1,3) );

                if (currentSegment>99) o += strHundreds[firstDigit]+" "; // hundreds
                if (secondAndThirdDigit>20) {// >20
                    o += strTens[secondDigit]+" ";
                    o += kind[ sexi ][thirdDigit]+" ";
                }
                else { // <=20
                    if (secondAndThirdDigit>9) o += strTeens[secondAndThirdDigit-9]+" "; // 10-20
                    else o += kind[ sexi ][thirdDigit]+" "; // 0-9
                }
                // Units (rubles, dollars...)
                o += morph(currentSegment, forms[level][ 0],forms[level][1],forms[level][2])+" ";
                level--;
            }
            // Kops in digital representation
            if (stripkop) {
                o = o.replaceAll(" {2,}", " ");
            }
            else {
                o = o+""+kops+" "+morph(kop,forms[ 0][ 0],forms[ 0][1],forms[ 0][2]);
                o = o.replaceAll(" {2,}", " ");
            }
            return o;
        }

        /**
         * Склоняем словоформу
         * @param n Long количество объектов
         * @param f1 String вариант словоформы для одного объекта
         * @param f2 String вариант словоформы для двух объектов
         * @param f5 String вариант словоформы для пяти объектов
         * @return String правильный вариант словоформы для указанного количества объектов
         */
        public String morph(long n, String f1, String f2, String f5) {
            n = Math.abs(n) % 100;
            long n1 = n % 10;
            if (n > 10 && n < 20) return f5;
            if (n1 > 1 && n1 < 5) return f2;
            if (n1 == 1) return f1;
            return f5;
        }

        public String beginFromUpper(String str){
            return str.substring(0,1).toUpperCase() + str.substring(1); //convert first letter to uppercase
        }

    }

