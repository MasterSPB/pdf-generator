package ru.onyx.clipper.data;

import com.itextpdf.text.Image;

import java.util.Date;

/**
 * User: MasterSPB
 * Date: 15.03.12
 * Time: 22:22
 */
public interface PropertyGetter {
    public String GetProperty(String pName);
    public int GetPageCount(String pName);
    public Date GetPropertyAsDate(String pName);
    public Date GetPropertyStringAsDate(String pName, String pDateFormat);
    public Image GetImage(String fileName , String folderName,String fileType);
}
