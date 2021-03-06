package ru.onyx.clipper.pdfgenerator.data;

import com.itextpdf.text.Image;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by anton on 03.04.14.
 */
public class PropertyGetterFromJSONStringImpl implements PropertyGetter {

    private  Logger logger;

    private String jsonPlainString;

    public PropertyGetterFromJSONStringImpl(String jsonPlainString) {
        this.jsonPlainString = jsonPlainString;
    }

    @Override
    public String GetProperty(String pName) {
        String jsonPath = pName;
        try {
            Object value = JsonPath.read(jsonPlainString, jsonPath);
            if (value != null) {
                if (value.equals("undefined")) {
                    return "";
                }
                return value.toString();
            }
            return null;
        } catch (PathNotFoundException e) {
            if (jsonPath.contains("[0]")) {
                int firstBracketIndex = jsonPath.lastIndexOf("[0");
                int lastBracketIndex = jsonPath.lastIndexOf("0]") + 1;
                StringBuilder temp = new StringBuilder(jsonPath.substring(0, firstBracketIndex));
                temp.append(jsonPath.substring(lastBracketIndex + 1, jsonPath.length()));
                return GetProperty(temp.toString());
            } else {
                log(e.getMessage());
            }
            return null;
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            return null;
        }
    }

    @Override
    public int GetPageCount(String pName) {
        int i = 0;
        try {
            Object o = JsonPath.read(jsonPlainString, pName);
            if (o instanceof net.minidev.json.JSONObject) {
                return 1;
            }
            JSONArray ja = (JSONArray) o;
            i = ja.size();
        } catch (NullPointerException n) {
            n.printStackTrace();
            return 0;
        } catch (ClassCastException cce) {
            cce.printStackTrace();
            return -1;
        }
        return i;
    }

    @Override
    public Date GetPropertyAsDate(String pName) {
        try {
            String dateAsString = GetProperty(pName);
            final String pattern = "yyyyMMdd'T'hhmmss";
            final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(dateAsString);
        } catch (Exception e) {
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
        if (propvalue.equals("null")) return null;
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
            e.printStackTrace();
            return null;
        }
    }

    void log(String message) {
        if (logger != null) logger.debug(message);
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}