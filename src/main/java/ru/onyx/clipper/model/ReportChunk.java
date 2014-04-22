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

        if (content.equalsIgnoreCase("true")) {
            content = "\uf0FE";
        }
        if (content.equalsIgnoreCase("false")) {
            content = "\uf0A8";
        }

        Chunk ch = new Chunk(content);

        Font f = getFont();

        int[] color = getTextColor();
        if(color != null) f.setColor(color[0],color[1],color[2]);

        if (f != null) ch.setFont(f);
        return ch;
    }



}

