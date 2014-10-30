package ru.onyx.clipper.model;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import org.w3c.dom.Node;
import ru.onyx.clipper.data.PropertyGetter;
import ru.onyx.clipper.utils.ReportCalcUtils;
import ru.onyx.clipper.utils.ReportStrUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * User: Alex
 * Date: 14.03.12
 * Time: 17:31
 */
public class ReportChunk extends BaseReportObject {

    private String extraAttribute;

    public ReportChunk(Node node, HashMap<String, ReportBaseFont> fonts, BaseReportObject pParent, PropertyGetter pGetter) throws ParseException, IOException, DocumentException {
        _fonts = fonts;
        parent = pParent;
        propertyGetter = pGetter;
        Load(node);
    }

    @Override
    public Element getPdfObject() {
        String content = "";
        String key,strEl;
        double res;


        if (this.text != null) content = this.text;

        if (getPropertyName() != null && getPropertyName().length() > 0) {
            content = propertyGetter.GetProperty(getPropertyName());

            if (getDateFormat() != null && getToDateFormat() != null) {
                content = ConvertPropertyToSpecificDateFormat(propertyGetter.GetProperty(getPropertyName()));
            }
        }


        if(content==null && getPropertyName()!=null && !getPropertyName().contains("$") && parent.parent.parent.getPageNameRT()!=null){
            content = propertyGetter.GetProperty(parent.parent.parent.getPageNameRT()+getPropertyName());
        }

        try {
            if (getPropertyCalc() != null && !getPropertyCalc().isEmpty()) {
                String calcExpression = getPropertyCalc();
                String calcProp = "";
                StringTokenizer st = new StringTokenizer(calcExpression, "()+-*/", true);
                while (st.hasMoreElements()) {
                    key = st.nextToken();
                    if (key.equals("+")) {
                        calcProp += key;
                    } else if (key.equals("-")) {
                        calcProp += key;
                    } else if (key.equals("*")) {
                        calcProp += key;
                    } else if (key.equals("/")) {
                        calcProp += key;
                    } else if (key.equals("(")) {
                        calcProp += key;
                    } else if (key.equals(")")) {
                        calcProp += key;
                    } else if (key.contains("$")) {
                        strEl = propertyGetter.GetProperty(key);
                        if (strEl != null) {
                            calcProp += strEl;
                        } else {
                            calcProp += 0;
                        }
                    } else {
                        calcProp += key;
                    }
                }

                res = ReportCalcUtils.calculate(calcProp);

                content = String.format("%,6.2f", res);

                if (res == 0) {
                    content = "-";
                }

                if (content.length() == 0) {
                    content = "-";
                }


            }
        } catch (Throwable throwable) {
            content = "Ошибка вычисления";
        }

		if(getIsInitial().equals("true") && content != null && content != ""){
			Character init = content.charAt(0);
			content = init.toString().toUpperCase();
		}

        if (getDelimiterAdd() != null && content != null && !content.equals("")) {
			if(getDelimiterAfter().equals("true")){
				content = content + getDelimiterAdd();
			}else {
				content = getDelimiterAdd() + content;
			}
        }

		if(getIfZero()!=null && content != null){
			try{
				if(content.trim().equals("0,00") || content.trim().equals("0.00") || content.trim().equals("0")){
					content = getIfZero();
				}
				if(content.equals("null")){
					content = null;
				}
			}catch (Exception ne){
			}
		}



        Font NullF = null;
        if (content == null || content.equals("")) {
            content = getDefaultNullValue();
            NullF = getNullFont();
        }

        if (content.equalsIgnoreCase("true")) {
            content = "\uf0FE";
        }
        if (content.equalsIgnoreCase("false")) {
            content = "\uf0A8";
        }

        if (getTextCase() != null) {
            if (getTextCase().equals("upper")) content = content.toUpperCase();
            if (getTextCase().equals("lower")) content = content.toLowerCase();
        }

        if (getStringformat() != null && !content.isEmpty() && !content.equals("-")) {
            try {
                if (getLocaleDel() != null && getLocaleDel().equals(".")) {
                    content = String.format(Locale.ENGLISH, getStringformat(), Double.parseDouble(content));
                } else {
                    if (getStringformat().equals("tenth")) {
                        content = content.substring(content.indexOf(".") + 1);
                    } else {
						Locale locale = new Locale("ru");
                        content = String.format(locale, getStringformat(), Double.parseDouble(content));
                    }
                }
            } catch (NumberFormatException e) {
                content = "";
            }
        }



        if (getNegativeEmbrace()) {
            content = ReportStrUtils.embraceNegativeValue(content);
        }

        if (getDecimalSeparator() != null) {
            content = ReportStrUtils.replaceDecSeparator(content, getDecimalSeparator());
        }

        if (getLastTableRowCount()) {
            content = String.valueOf(Report.getLastTableRowCount());
        }

        Chunk ch = new Chunk(content);

        if (getChunkIndex().equals("upper")) {
            ch.setTextRise(ch.getFont().getCalculatedSize() / 5);

        } else if (getChunkIndex().equals("lower")) {
            ch.setTextRise(-ch.getFont().getCalculatedSize() / 5);
        }

        Font f = getFont();


        int[] color = getTextColor();
        if (color != null) f.setColor(color[0], color[1], color[2]);

        if (f != null && getNullFontStyle() == null && NullF == null) ch.setFont(f);
        else ch.setFont(NullF);

        if (getCharspacing() > 0) {
            ch.setCharacterSpacing(getCharspacing());
        }

        return ch;
    }

    public void setExtraAttribute(String _val){
        extraAttribute = _val;
    }

    public String getExtraAttribute() {
        return extraAttribute;
    }
}

