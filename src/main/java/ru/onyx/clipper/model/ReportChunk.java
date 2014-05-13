package ru.onyx.clipper.model;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import org.w3c.dom.Node;
import ru.onyx.clipper.data.PropertyGetter;

import java.util.HashMap;

/**
 * User: Alex
 * Date: 14.03.12
 * Time: 17:31
 */
public class ReportChunk extends BaseReportObject {

    public ReportChunk(Node node, HashMap<String, ReportBaseFont> fonts, BaseReportObject pParent, PropertyGetter pGetter) {
        _fonts = fonts;
        parent = pParent;
        propertyGetter = pGetter;
        Load(node);
    }


    @Override
    public Element getPdfObject() {
        String content = "";

        if (this.text != null) content = this.text;

        if (getPropertyName() != null && getPropertyName().length() > 0) {
            content = propertyGetter.GetProperty(getPropertyName());

            if (getDateFormat() != null && getToDateFormat() != null) {
                content = ConvertPropertyToSpecificDateFormat(propertyGetter.GetProperty(getPropertyName()));
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

