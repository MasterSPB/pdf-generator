package ru.onyx.clipper.pdfgenerator.model;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import org.w3c.dom.Node;
import ru.onyx.clipper.pdfgenerator.data.PropertyGetter;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

/**
 * User: MasterSPB
 * Date: 14.03.12
 * Time: 10:31
 */
public class ReportParagraph extends BaseReportObject {

    public ReportParagraph(Node node,HashMap<String ,ReportBaseFont> fonts,BaseReportObject pParent,PropertyGetter pGetter,Report report) throws ParseException, IOException, DocumentException {
     _fonts = fonts;
      parent = pParent;
      propertyGetter = pGetter;
      this.report=report;
      Load(node);
      LoadItems(node,fonts,this,pGetter);
        if(pParent != null && pParent.getPageNameRT() != null){
            setPageNameRT(pParent.getPageNameRT());
        }
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

    protected Float getVerticalSize() throws DocumentException, ParseException, IOException {
        Paragraph par = (Paragraph) this.getPdfObject();
        return par.getLeading();
    }
}
