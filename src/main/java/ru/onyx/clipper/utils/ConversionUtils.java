package ru.onyx.clipper.utils;

import ru.onyx.clipper.converter.JSONException;
import ru.onyx.clipper.converter.JSONObject;
import ru.onyx.clipper.converter.XML;

import java.io.*;

/**
 * Created by anton on 30.04.14.
 * Utils to convert files between formats
 */
public class ConversionUtils {
    public static int PRETTY_PRINT_INDENT_FACTOR = 4;

    public static String ByteXMLtoJSON(byte[] XMLContent) throws UnsupportedEncodingException {
        String XMLString = new String(XMLContent, "UTF-8");

        try {
            JSONObject xmlJSONObj = XML.toJSONObject(XMLString);
            String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
            return jsonPrettyPrintString;
        } catch (JSONException je) {
            System.out.println(je.toString());

            return null;
        }
    }

    public static void XMLtoJSON(String pathToXML, String pathToJSON){

        try {
            try(BufferedReader br = new BufferedReader(new FileReader(pathToXML))) {
                StringBuilder sb = new StringBuilder();
                String line = null;

                try {
                    line = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    try {
                        line = br.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    JSONObject xmlJSONObj = XML.toJSONObject(sb.toString());
                    String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);

                    try {

                        File file = new File(pathToJSON);

                        // if file doesnt exist, then create it
                        if (!file.exists()) {
                            file.createNewFile();
                        }

                        FileWriter fw = new FileWriter(file.getAbsoluteFile());
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(jsonPrettyPrintString);
                        bw.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } catch (JSONException je) {
                    System.out.println(je.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}