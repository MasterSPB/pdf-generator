package ru.onyx.clipper.pdfgenerator.model;

import com.itextpdf.text.*;
import org.w3c.dom.Node;
import ru.onyx.clipper.pdfgenerator.data.PropertyGetter;
import ru.onyx.clipper.pdfgenerator.utils.ReportCalcUtils;
import ru.onyx.clipper.pdfgenerator.utils.ReportStrUtils;

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

    public void setCustomText(String text) {
        this.customtext = text;
    }

    public ReportChunk(Node node, HashMap<String, ReportBaseFont> fonts, BaseReportObject pParent, PropertyGetter pGetter,Report report) throws ParseException, IOException, DocumentException {
        _fonts = fonts;
        parent = pParent;
        propertyGetter = pGetter;
        this.report=report;

        Load(node);

        if(!this.jsFunction.equals("")) {
           eval(jsFunction, node, pGetter);
        }

        LoadItems(node, fonts, this, pGetter);
        if (pParent != null && pParent.getPageNameRT() != null) {
            setPageNameRT(pParent.getPageNameRT());
        }

    }

    @Override
    public Element getPdfObject() {
        String content = "";
        String key,strEl;
        double res;

        if (this.text != null) content = this.text;

        if (getPropertyName() != null && getPropertyName().length() > 0 && customtext == null) {
            content = propertyGetter.GetProperty(getPropertyName());

            if (getDateFormat() != null && getToDateFormat() != null) {
                content = ConvertPropertyToSpecificDateFormat(propertyGetter.GetProperty(getPropertyName()));
            }
        }


        if(content==null && getPropertyName()!=null && !getPropertyName().contains("$") && parent.parent.parent.getPageNameRT()!=null){
            content = propertyGetter.GetProperty(parent.parent.parent.getPageNameRT()+getPropertyName());
        }
        if(content==null && getPropertyName()!=null && getPropertyName().contains("@") && report!=null){
            if (getPropertyName().equals("@currentpage")) {
                content=Integer.toString(report.getPageNumber());
            } else if (getPropertyName().equals("@pagecount")) {
                try {
                    Chunk ch = new Chunk(Image.getInstance(report.getTotalPageCountTemplate()),0,0);

                    if (getChunkIndex().equals("upper")) {
                        ch.setTextRise(ch.getFont().getCalculatedSize() / 5);

                    } else if (getChunkIndex().equals("lower")) {
                        ch.setTextRise(-ch.getFont().getCalculatedSize() / 5);
                    }

                    Font f = getFont();


                    int[] color = getTextColor();
                    if (color != null) f.setColor(color[0], color[1], color[2]);

                    if (getCharspacing() > 0) {
                        ch.setCharacterSpacing(getCharspacing());
                    }
                    return ch;
                } catch (BadElementException be) {
                    be.printStackTrace();
                }
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

		if (getStringformat() != null && content!=null && !content.isEmpty() && !content.equals("-")) {
			try {
				if (getLocaleDel() != null && getLocaleDel().equals(".")) {
					content = String.format(Locale.ENGLISH, getStringformat(), Double.parseDouble(content));
				} else {
					if (getStringformat().equals("tenth")) {
						content = content.substring(content.indexOf(".") + 1);
					}else if(getStringformat().equals("integral")){
						content = String.format(new Locale("ru"), "%,6.0f", Double.parseDouble(content.substring(0, content.indexOf("."))));
					} else if (getStringformat().contains("d")) {
                        Locale locale = new Locale("ru");
                        content = String.format(locale, getStringformat(), Integer.parseInt(content));
                    }
					else {
						Locale locale = new Locale("ru");
						content = String.format(locale, getStringformat(), Double.parseDouble(content));
					}
				}
			} catch (NumberFormatException e) {
				content = "";
			}
		}


        // Some code to do
        if (customtext != null) {
            content = customtext;
            if (getDateFormat() != null && getToDateFormat() != null) {
                content = ConvertPropertyToSpecificDateFormat(customtext);
            }

            if (getStringformat() != null && content!=null && !content.isEmpty() && !content.equals("-")) {
                try {
                    if (getLocaleDel() != null && getLocaleDel().equals(".")) {
                        content = String.format(Locale.ENGLISH, getStringformat(), Double.parseDouble(content));
                    } else {
                        if (getStringformat().equals("tenth")) {
                            content = content.substring(content.indexOf(".") + 1);
                        }else if(getStringformat().equals("integral")){
                            content = String.format(new Locale("ru"), "%,6.0f", Double.parseDouble(content.substring(0, content.indexOf("."))));
                        }
                        else {
                            Locale locale = new Locale("ru");
                            content = String.format(locale, getStringformat(), Double.parseDouble(content));
                        }
                    }
                } catch (NumberFormatException e) {
                    content = "";
                }
            }

            if (getDecimalSeparator() != null) {
                content = ReportStrUtils.replaceDecSeparator(content, getDecimalSeparator());
            }
        }



		if(getIfZero()!=null && content != null){
			try{
				if(content.trim().equals("0,00") || content.trim().equals("0.00") || content.trim().equals("0")|| content.trim().equals("00")){
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

