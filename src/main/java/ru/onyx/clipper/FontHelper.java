package ru.onyx.clipper;

import java.util.HashMap;

/**
 * User: MasterSPB
 * Date: 20.03.12
 * Time: 12:01
 */
public class FontHelper {

    public static final String Fonts = "Fonts";

    public static HashMap<String, byte[]> GetFonts(String fontNames) {
        String[] fonts = fontNames.split(",");
        HashMap<String, byte[]> result = new HashMap<String, byte[]>();

        for (String fontName : fonts) {
            result.put(fontName, GetBinaryContent(fontName, Fonts));
        }
        return result;
    }

    private static byte[] GetBinaryContent(String fontName , String folderName) {
//        PublicAPI tools = ThreadContainer.get().getPublicAPI();
//
//
//        ClipboardPage rulefile = tools.createPage("Rule-File-Binary","");
//        rulefile.putString("pyApplicationName",folderName);
//        rulefile.putString("pyFileName", fontName);
//        rulefile.putString("pyFileType", "ttf");
//
//        rulefile = tools.getDatabase().open(rulefile, true);
//        return Base64Util.decodeToByteArray(rulefile.getString("pyFileSource"));


        return null;
    }

    public static byte[] GetBinaryContent(String fontName , String folderName,String fileType) {
//        PublicAPI tools = ThreadContainer.get().getPublicAPI();
//
//
//
//        ClipboardPage rulefile = tools.createPage("Rule-File-Binary","");
//        rulefile.putString("pyApplicationName",folderName);
//        rulefile.putString("pyFileName", fontName);
//        rulefile.putString("pyFileType", fileType);
//
//        rulefile = tools.getDatabase().open(rulefile, true);
//        return Base64Util.decodeToByteArray(rulefile.getString("pyFileSource"));

        return null;
    }

}
