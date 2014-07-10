package ru.onyx.clipper.utils;

import com.itextpdf.text.Document;

/**
 * Created by anton on 27.06.14.
 */
public class DocumentUtils {
    public static float calcFreeSpace(Float elementSize, Float freeSpaceBefore, Document _doc) {
        System.out.println(_doc.toString());
        final float pageSize = _doc.getPageSize().getHeight() - _doc.topMargin() - _doc.bottomMargin();
        if(elementSize<=freeSpaceBefore) return freeSpaceBefore - elementSize; //free space left
        else {
            if(elementSize < pageSize) {
                return pageSize - elementSize - freeSpaceBefore; // free space left on the next page
            } else {
                return pageSize - (elementSize % pageSize) - freeSpaceBefore; // free space left on one of the next pages since element is bigger than the page
            }
        }
    }
}
