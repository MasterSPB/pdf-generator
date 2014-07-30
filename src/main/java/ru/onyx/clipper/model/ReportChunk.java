package ru.onyx.clipper.model;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import org.w3c.dom.Node;
import ru.onyx.clipper.data.PropertyGetter;
import ru.onyx.clipper.utils.ReportCalcUtils;
import ru.onyx.clipper.utils.ReportStrUtils;

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

    public ReportChunk(Node node, HashMap<String, ReportBaseFont> fonts, BaseReportObject pParent, PropertyGetter pGetter) throws ParseException {
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

        if (getDelimiterAdd() != null && content != null) {
            content = getDelimiterAdd() + content;
        }

        Font NullF = null;
        if (content == null) {
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
                    content = String.format(getStringformat(), Double.parseDouble(content));
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


}

