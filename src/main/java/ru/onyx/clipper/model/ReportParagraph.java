package ru.onyx.clipper.model;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import org.w3c.dom.Node;
import ru.onyx.clipper.data.PropertyGetter;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

/**
 * User: MasterSPB
 * Date: 14.03.12
 * Time: 10:31
 */
public class ReportParagraph extends BaseReportObject {

    public ReportParagraph(Node node,HashMap<String ,ReportBaseFont> fonts,BaseReportObject pParent,PropertyGetter pGetter) throws ParseException {
     _fonts = fonts;
      parent = pParent;
      propertyGetter = pGetter;
      Load(node);
      LoadItems(node,fonts,this,pGetter);
    }


    @Override
    public Element getPdfObject() throws DocumentException, ParseException, IOException {
        Paragraph par = new Paragraph();
        if(getFont() != null) par.setFont(getFont());
        par.setAlignment(getHorizontalTextAlignment());

        if(getSpacingAfter() >= 0) par.setSpacingAfter(getSpacingAfter());
        if(getSpacingBefore() >= 0) par.setSpacingBefore(getSpacingBefore());
        if(getLeading() >= 0) par.setLeading(getLeading());
        if(getIndentationLeft() >= 0) par.setIndentationLeft(getIndentationLeft());
        if(getIndentationRight() >= 0) par.setIndentationRight(getIndentationRight());
        if(getFirstLineIndentation() >= 0) par.setFirstLineIndent(getFirstLineIndentation());

        if(this.items.size() >0) {
            for (BaseReportObject item : this.items) {
                par.add(item.getPdfObject());
            }
        }

        return par;
    }
}
