package ru.onyx.clipper.pdfgenerator.data;

import com.itextpdf.text.Image;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import net.minidev.json.JSONArray;

import java.io.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by anton on 03.04.14.
 */
public class PropertyGetterFromJSONFileImpl implements PropertyGetter{

    private static final Logger logger= LoggerFactory.getLogger(PropertyGetterFromJSONFileImpl.class);


    private File jsonFile;

    public PropertyGetterFromJSONFileImpl(String path) {
        jsonFile = new File(path);
    }

    public PropertyGetterFromJSONFileImpl(File jsonFile1) {
        this.jsonFile = jsonFile1;
    }

    @Override
    public String GetProperty(String pName) {
        String jsonPath = pName;
        try{
            Object value = JsonPath.read(jsonFile, jsonPath);
            if (value != null){
                if(value.equals("undefined")){
                    return "";
                }
                return value.toString();
            }
            return null;
        } catch (IOException e){
            e.printStackTrace();
        } catch (PathNotFoundException e2) {
			if(jsonPath.contains("[0]")){
				int firstBracketIndex = jsonPath.lastIndexOf("[0");
				int lastBracketIndex = jsonPath.lastIndexOf("0]")+1;
				StringBuilder temp = new StringBuilder(jsonPath.substring(0,firstBracketIndex));
				temp.append(jsonPath.substring(lastBracketIndex + 1, jsonPath.length()));
				return GetProperty(temp.toString());
			} else {
                logger.debug(e2.getMessage());
            }
            return null;
        }catch (IllegalArgumentException iae){
			iae.printStackTrace();
            return null;
        }
        return null;
    }



    @Override
    public int GetPageCount(String pName) {
        int i = 0;
        try{
            Object o =  JsonPath.read(jsonFile, pName);
			if (o instanceof net.minidev.json.JSONObject){
				return 1;
			}
			JSONArray ja= (JSONArray) o; // throws ClassCastException when it is not JsonArray. returns -1. This part needs to be refactored.
            if (ja!=null)
                i = ja.size();
        }catch (NullPointerException n) {
			n.printStackTrace();
            return 0;
        }catch (ClassCastException cce){
			cce.printStackTrace();
            return -1;
        }catch (IOException e){
            e.printStackTrace();
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
        if (propvalue.equals(" ")) return null;
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