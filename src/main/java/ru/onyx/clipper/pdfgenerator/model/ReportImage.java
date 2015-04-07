package ru.onyx.clipper.pdfgenerator.model;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import org.w3c.dom.Node;
import ru.onyx.clipper.pdfgenerator.data.PropertyGetter;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

/**
 * User: Alex
 * Date: 04.07.12
 * Time: 10:33
 */
public class ReportImage extends BaseReportObject {

    public ReportImage(Node node, HashMap<String, ReportBaseFont> fonts, BaseReportObject pParent, PropertyGetter pGetter) throws ParseException{
        _fonts = fonts;
        parent = pParent;
        propertyGetter = pGetter;
        Load(node);
    }

    public Image getPdfObject() throws DocumentException, ParseException,IOException {

        String fileName = getFileName();
        String fileFolder = getFileFolder();
        String fileType = getFileType();

        if(fileName == null || fileFolder == null || fileType == null) return null;

       // byte[] imgContent =  getImageContent(fileName,fileFolder,fileType);

        Image img = propertyGetter.GetImage(fileName,fileFolder,fileType);

        if(img == null) return null;

        float[] scaleab = getScaleAbsolute();
            if(scaleab != null) {
                img.scaleAbsolute(scaleab[0],scaleab[1]);  }
        float[] scaleper = getScalePercent();
        if(scaleper != null) {
            img.scalePercent(scaleper[0],scaleper[1]);
        }


        if(getSpacingAfter() >= 0) img.setSpacingAfter(getSpacingAfter());
        if(getSpacingBefore() >= 0) img.setSpacingBefore(getSpacingBefore());

        String position = getPosition();
        if(position.equals("absolute")) {
            float[] coords = getCoordinates();
            if(coords != null) {
                img.setAbsolutePosition(coords[0],coords[1]);
            }
        }



        return img;
    }

}
