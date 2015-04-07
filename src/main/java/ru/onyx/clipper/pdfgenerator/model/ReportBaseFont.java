package ru.onyx.clipper.pdfgenerator.model;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;

import java.io.IOException;

/**
 * User: MasterSPB
 * Date: 13.03.12
 * Time: 18:28
 */
public class ReportBaseFont {
    private String name;
    private String fontPath;
    private BaseFont baseFont;

    public ReportBaseFont(String pFontName, String pFontPath, byte[] fileContent) throws IOException, DocumentException {
        name = pFontName;
        fontPath = pFontPath;
        baseFont  = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED, BaseFont.CACHED, fileContent, fileContent);
    }


    public String getName() {
        return name;
    }    
    public String getFontPath() {
        return fontPath;
    }

    public Font getCustomFont(float weight) {
        Font font = new Font(baseFont,weight);
        return font;
    }

     public Font getCustomFont(float weight,int style) {
        Font font = new Font(baseFont,weight,style);
        return font;
    }

    public BaseFont getBaseFont(){
        return baseFont;
    }
}
