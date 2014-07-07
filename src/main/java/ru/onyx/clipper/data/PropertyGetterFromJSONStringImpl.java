package ru.onyx.clipper.data;

import com.itextpdf.text.Image;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import net.minidev.json.JSONArray;

import ru.onyx.clipper.data.PropertyGetter;

import java.io.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;


/**
 * Created by anton on 03.04.14.
 */
public class PropertyGetterFromJSONStringImpl implements PropertyGetter{
    private String jsonPlainString;

    public PropertyGetterFromJSONStringImpl(String jsonPlainString) {
        this.jsonPlainString = jsonPlainString;
    }

    @Override
    public String GetProperty(String pName) {
        String jsonPath = pName;
            try{
            Object value = JsonPath.read(jsonPlainString, jsonPath);
            if (value != null) {
                return value.toString();
            }
            return null;

            } catch (PathNotFoundException e) {
                return null;
            }
    }

    @Override
    public int GetPageCount(String pName) {
        int i = 0;
        JSONArray ja =  JsonPath.read(jsonPlainString, pName);
        i = ja.size();
        return i;
    }

    @Override
    public Date GetPropertyAsDate(String pName) {
        try {
            String dateAsString = GetProperty(pName);
            final String pattern = "yyyyMMdd'T'hhmmss";
            final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(dateAsString);
        } catch (Exception e)  {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Date GetPropertyStringAsDate(String propvalue, String pDateFormat) {
        if (pDateFormat == null) return null;
        DateFormat df = new SimpleDateFormat(pDateFormat);
        if (propvalue == null) return null;
        if (propvalue.length() == 0) return null;
        if (propvalue == "null") return null;
        try {
            return df.parse(propvalue);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Image GetImage(String fileName, String folderName, String fileType) {
        try {
            String fullName = folderName + "\\" + fileName + "." + fileType;
            return Image.getInstance(fullName);
        } catch (Exception e) {
            return null;
        }
    }
}