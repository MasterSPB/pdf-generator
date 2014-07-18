package ru.onyx.clipper.model;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import org.w3c.dom.Node;
import ru.onyx.clipper.data.PropertyGetter;
import ru.onyx.clipper.utils.CalcUtils;
import ru.onyx.clipper.utils.StrUtils;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * User: Alex
 * Date: 14.03.12
 * Time: 17:31
 */
public class ReportChunk extends BaseReportObject {

    public ReportChunk(Node node, HashMap<String, ReportBaseFont> fonts, BaseReportObject pParent, PropertyGetter pGetter) throws ParseException{
        _fonts = fonts;
        parent = pParent;
        propertyGetter = pGetter;
        Load(node);
    }


    @Override
    public Element getPdfObject() {
        String content = "";
        String key,keyT,strEl;
        double res = 0.0f;
        String result="";

        if (this.text != null) content = this.text;

        if (getPropertyName() != null && getPropertyName().length() > 0) {
            content = propertyGetter.GetProperty(getPropertyName());

            if (getDateFormat() != null && getToDateFormat() != null) {
                content = ConvertPropertyToSpecificDateFormat(propertyGetter.GetProperty(getPropertyName()));
            }
        }


        if(getPropertyCalc()!=null && getPropertyCalc().length()>0){
            String calcExpression = propertyGetter.GetProperty(getPropertyCalc());
            String calcProp="";
            StringTokenizer st = new StringTokenizer(calcExpression, "()+-*/", true);
            while (st.hasMoreElements()){
                key= st.nextToken();
                if(key.equals("+")){
                    calcProp += key;
                }else if(key.equals("-")){
                    calcProp += key;
                }else if(key.equals("*")){
                    calcProp += key;
                }else if(key.equals("/")){
                    calcProp += key;
                }else if(key.equals("(")){
                    calcProp += key;
                }else if(key.equals(")")){
                    calcProp += key;
                }else if(key.contains("$")){
                    strEl=propertyGetter.GetProperty(key);
                    if(strEl!=null) {
                        calcProp += strEl;
                    }else{
                        calcProp += 0;
                    }
                }else{
                    calcProp += key;
                }
            }


            /*
            * if(getPropertyCalc()!=null && getPropertyCalc().length()>0){
            String calcExpression = propertyGetter.GetProperty(getPropertyCalc());
            Stack<Double> exprEl = new Stack<>();
            StringTokenizer st = new StringTokenizer(calcExpression, "+-*//*", true);
            while (st.hasMoreElements()){
                key= st.nextToken();
                if(key.equals("+")){

                }else if(key.equals("-")){
                    keyT = st.nextToken();
                    strEl=propertyGetter.GetProperty(keyT);
                    try {
                        double element1 = Double.parseDouble(strEl);
                        exprEl.push(-element1);
                    }catch (NullPointerException ne){
                        continue;
                    }
                }else if(key.equals("*")){
                    double element1 = exprEl.pop();
                    keyT = st.nextToken();
                    strEl=propertyGetter.GetProperty(keyT);
                    try {
                        double element2 = Double.parseDouble(strEl);
                        exprEl.push(element1 * element2);
                    }catch (NullPointerException ne){
                        continue;
                    }
                }else if(key.equals("/")){
                    double element1 = exprEl.pop();
                    keyT = st.nextToken();
                    strEl=propertyGetter.GetProperty(keyT);
                    try {
                        double element2 = Double.parseDouble(strEl);
                        exprEl.push(element1 / element2);
                    }catch (NullPointerException ne){
                        continue;
                    }
                }else{
                    strEl=propertyGetter.GetProperty(key);
                    try {
                        double element1 = Double.parseDouble(strEl);
                        exprEl.push(element1);
                    }catch (NullPointerException ne){
                        continue;
                    }
                }
            }*/

            CalcUtils cu = new CalcUtils();


            content += cu.calculate(calcProp);

            if (content.length() == 0) {
                content = "-";
            }
        }

        Font NullF = null;
        if(content == null){
            content = getDefaultNullValue();
            NullF = getNullFont();
        }

        if (content.equalsIgnoreCase("true")) {
            content = "\uf0FE";
        }
        if (content.equalsIgnoreCase("false")) {
            content = "\uf0A8";
        }

        if(getTextCase() != null) {
            if (getTextCase().equals("upper")) content=content.toUpperCase();
            if (getTextCase().equals("lower")) content=content.toLowerCase();
        }

        if (getNegativeEmbrace()){
            content = StrUtils.embraceNegativeValue(content);
        }

        if (getDecimalSeparator() != null) {
            content = StrUtils.replaceDecSeparator(content, getDecimalSeparator());
        }

        Chunk ch = new Chunk(content);

        if(getChunkIndex().equals("upper")){
            ch.setTextRise(ch.getFont().getCalculatedSize()/5);

        } else if(getChunkIndex().equals("lower")){
            ch.setTextRise(-ch.getFont().getCalculatedSize()/5);
        }

        Font f = getFont();


        int[] color = getTextColor();
        if(color != null) f.setColor(color[0],color[1],color[2]);

        if (f != null && getNullFontStyle()==null && NullF==null) ch.setFont(f);
        else ch.setFont(NullF);

        if (getCharspacing() > 0) {
            ch.setCharacterSpacing(getCharspacing());
        }

        return ch;
    }



}

