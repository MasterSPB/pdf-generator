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
        double res;

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

            CalcUtils cu = new CalcUtils();

            res = cu.calculate(calcProp);
            content = String.format("%,6.2f",res);

            if(res==0){
                content = "-";
            }

            if (content.length() == 0) {
                content = "-";
            }


        }

        if(getDelimiterAdd()!=null&&getPropertyName()!=null){
            content+=getDelimiterAdd();
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

        if (getStringformat() != null && !content.equals("") && !content.equals("-")) {
            content = String.format(getStringformat(), Double.parseDouble(content));
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

