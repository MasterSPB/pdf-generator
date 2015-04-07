package ru.onyx.clipper.pdfgenerator.utils;

import ru.onyx.clipper.pdfgenerator.converter.JSONException;
import ru.onyx.clipper.pdfgenerator.converter.JSONObject;
import ru.onyx.clipper.pdfgenerator.converter.XML;

import java.io.*;

/**
 * Created by anton on 30.04.14.
 * Utils to convert files between formats
 */
public class ReportConversionUtils {
    public static int PRETTY_PRINT_INDENT_FACTOR = 4;

    public static String ByteXMLtoJSON(byte[] XMLContent, String encoding) throws UnsupportedEncodingException {
        String XMLString = new String(XMLContent, encoding);

        try {
            JSONObject xmlJSONObj = XML.toJSONObject(XMLString);
            String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
            return jsonPrettyPrintString;
        } catch (JSONException je) {
            System.out.println(je.toString());

            return null;
        }
    }

    public static String convert(String s, String encoding) throws UnsupportedEncodingException {
        return new String(s.getBytes(encoding));
    }

    public static void XMLtoJSON(String pathToXML, String pathToJSON, String encoding){

        try {
            FileInputStream fis = new FileInputStream(pathToXML);
            InputStreamReader irs  = new InputStreamReader(fis, encoding);
            try(BufferedReader br = new BufferedReader(irs)) {
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