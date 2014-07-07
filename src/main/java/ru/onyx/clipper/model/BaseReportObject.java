package ru.onyx.clipper.model;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.onyx.clipper.data.PropertyGetter;
import ru.onyx.clipper.utils.DateUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static ru.onyx.clipper.model.Report.*;

/**
 * User: MasterSPB
 * Date: 13.03.12
 * Time: 18:26
 */
public abstract class BaseReportObject {

    public static final String COMPOSITE = "composite";

    public abstract Element getPdfObject() throws DocumentException, ParseException, IOException;

    protected HashMap<String, ReportBaseFont> _fonts;
    protected PropertyGetter propertyGetter;
    protected BaseReportObject parent;

    protected void Load(Node node) {
        NamedNodeMap attrObj = node.getAttributes();
        if (attrObj != null) {
            fontName = parseAttribute(attrObj, FONT_ATT_NAME, null);
            fontWeight = Float.parseFloat(parseAttribute(attrObj, FONT_WEIGHT_ATT_NAME, "-1"));
            leading = Float.parseFloat(parseAttribute(attrObj, LEADING_ATT_NAME, "-1"));
            widthpercentage = Float.parseFloat(parseAttribute(attrObj, WIDTH_PERCENTAGE_ATT_NAME, "-1"));

            String wcp = parseAttribute(attrObj, WIDTH_CELLS_PERCENTAGE_ATT_NAME, "");
            if (wcp.length() > 0) {
                String[] percs = wcp.split(",");
                Float[] cellsPercs = new Float[percs.length];
                for (int y = 0; y < percs.length; y++) {
                    cellsPercs[y] = Float.parseFloat(percs[y]);
                }
                widthcellspercentage = cellsPercs;
            }

            minFreeSpaceAfter = Integer.parseInt(parseAttribute(attrObj, MIN_FREE_SPACE_AFTER, "0"));
            aggrCol = Integer.parseInt(parseAttribute(attrObj, AGGR_COL, "0"));
            aggrFunc = parseAttribute(attrObj, AGGR_FUNC, "sum");
            pageNumType = parseAttribute(attrObj, PAGE_NUM_TYPE, "simple");
            pageHeader = parseAttribute(attrObj, PAGE_HEADER, "disabled");
            pageText = parseAttribute(attrObj, PAGE_TEXT, "null");
            textcase = parseAttribute(attrObj, TEXT_CASE, "null");
            charspacing = Integer.parseInt(parseAttribute(attrObj, CHARACTER_SPACING, "-1"));
            index = parseAttribute(attrObj, CHUNK_INDEX, null);
            negativeEmbrace = Boolean.parseBoolean(parseAttribute(attrObj, NEGATIVE_EMBRACE, null));
            borderstyle = parseAttribute(attrObj, BORDER_STYLE_ATT, null);
            reprowotherpageheight = Float.parseFloat(parseAttribute(attrObj, REPROW_OTHER_PAGE_HEIGHT, "-1f"));
            defaultnullvalue = parseAttribute(attrObj, DEFAULT_NULL_VALUE, "");
            reprowfpagerows = Integer.parseInt(parseAttribute(attrObj, REPROW_FPAGE_ROWS, "-1"));
            reprowotherpagerows = Integer.parseInt(parseAttribute(attrObj, REPROW_OTHER_PAGE_ROWS, "-1"));
            stringformat = parseAttribute(attrObj, STRING_FORMAT_ATT_NAME, null);
            columns = Integer.parseInt(parseAttribute(attrObj, COLUMNS_ATT_NAME, "1"));
            borderWidth = Float.parseFloat(parseAttribute(attrObj, BORDER_WIDTH_ATT_NAME, "-1"));
            borderWidthLeft = Float.parseFloat(parseAttribute(attrObj, BORDER_WIDTH_LEFT_ATT_NAME, "-1"));
            borderWidthRight = Float.parseFloat(parseAttribute(attrObj, BORDER_WIDTH_RIGHT_ATT_NAME, "-1"));
            borderWidthTop = Float.parseFloat(parseAttribute(attrObj, BORDER_WIDTH_TOP_ATT_NAME, "-1"));
            borderWidthBottom = Float.parseFloat(parseAttribute(attrObj, BORDER_WIDTH_BOTTOM_ATT_NAME, "-1"));
            spacingAfter = Float.parseFloat(parseAttribute(attrObj, SPACING_AFTER_ATT_NAME, "5"));
            spacingBefore = Float.parseFloat(parseAttribute(attrObj, SPACING_BEFORE_ATT_NAME, "-1"));
            useBorderPadding = Boolean.parseBoolean(parseAttribute(attrObj, USE_BORDER_PADDINGS_ATT_NAME, "false"));
            propertyName = parseAttribute(attrObj, PROPERTY_ATT_NAME, null);
            pageName = parseAttribute(attrObj, PAGE_NAME_ATT_NAME, null);
            colSpan = Integer.parseInt(parseAttribute(attrObj, COL_SPAN_ATT_NAME, "-1"));
            firstSymbols = Integer.parseInt(parseAttribute(attrObj, FIRST_SYMBOLS_YEAR_ATT_NAME, "-1"));
            lastSymbols = Integer.parseInt(parseAttribute(attrObj, LAST_SYMBOLS_YEAR_ATT_NAME, "-1"));
            rowSpan = Integer.parseInt(parseAttribute(attrObj, ROW_SPAN_ATT_NAME, "-1"));
            fixedHeight = Float.parseFloat(parseAttribute(attrObj, FIXED_HEIGHT_ATT_NAME, "-1"));
            minimumHeight = Float.parseFloat(parseAttribute(attrObj, MINIMUM_HEIGHT_ATT_NAME, "-1"));
            totalWidth = Float.parseFloat(parseAttribute(attrObj, TOTAL_WIDTH_ATT_NAME, "-1"));
            cellHeight = Float.parseFloat(parseAttribute(attrObj, CELL_HEIGHT_ATT_NAME, "-1"));
            text = node.getTextContent();
            customtext = parseAttribute(attrObj, CUSTOM_TEXT_RIGHT_ATT_NAME, null);
            wordalign = parseAttribute(attrObj, WORD_SPLITTER_ALIGN_ATT_NAME, "right");
            indentationLeft = Float.parseFloat(parseAttribute(attrObj, PARAGRAPH_INDENTATION_LEFT_ATT_NAME, "-1"));
            indentationRight = Float.parseFloat(parseAttribute(attrObj, PARAGRAPH_INDENTATION_RIGHT_ATT_NAME, "-1"));
            firstLineIndentation = Float.parseFloat(parseAttribute(attrObj, PARAGRAPH_FIRSTLINE_INDENT_ATT_NAME, "-1"));
            //stopInherit = Boolean.parseBoolean(parseAttribute(attrObj, STOP_INHERIT_ATT_NAME, "false"));
            propertyMode = parseAttribute(attrObj, PROPERTY_MODE_ATT_NAME, null);
            dateFormat = parseAttribute(attrObj, DATE_FORMAT_ATT_NAME, null);
            toDateFormat = parseAttribute(attrObj, TO_DATE_FORMAT_ATT_NAME, null);
            bgColor = parseAttribute(attrObj, BACKGROUNDCOLOR, null);
            borderColor = parseAttribute(attrObj, BORDERCOLOR, null);
            borderColorTop = parseAttribute(attrObj, BORDERCOLORTOP, null);
            borderColorBottom = parseAttribute(attrObj, BORDERCOLORBOTTOM, null);
            borderColorLeft = parseAttribute(attrObj, BORDERCOLORLEFT, null);
            borderColorRight = parseAttribute(attrObj, BORDERCOLORRIGHT, null);
            textColor = parseAttribute(attrObj, TEXTCOLOR, null);
            fileName = parseAttribute(attrObj, FILENAME, null);
            fileFolder = parseAttribute(attrObj, FILEFOLDER, null);
            fileType = parseAttribute(attrObj, FILETYPE, null);
            position = parseAttribute(attrObj, POSITION, "relative");
            bgimage = parseAttribute(attrObj, BGIMAGE, null);
            keepTogether = Boolean.parseBoolean(parseAttribute(attrObj, KEEPTOGETHER, "false"));
            replicateHeader = Boolean.parseBoolean(parseAttribute(attrObj, REPLICATE_HEADER, "false"));
            replicateFooter = Boolean.parseBoolean(parseAttribute(attrObj, REPLICATE_FOOTER, "false"));
            decseparator = parseAttribute(attrObj, DECIMAL_SEPARATOR, null);

            paddingLeft = -1f;
            paddingRight = -1f;
            paddingBottom = -1f;
            paddingTop = -1f;

            String paddings = parseAttribute(attrObj, PADDINGS_ATT_NAME, "");
            float[] padsf = parsePaddings(paddings);
            if (padsf != null) {
                paddingLeft = padsf[0];
                paddingRight = padsf[1];
                paddingTop = padsf[2];
                paddingBottom = padsf[3];
            }

            cellMode = parseAttribute(attrObj, CELL_MODE_ATT_NAME, null);

            coordinates = SetCoordinates(attrObj);
            scaleabsolute = SetScaleAbsolute(attrObj);
            scalepercent = SetScalePercent(attrObj);

            SetMonthFormat(attrObj);
            SetFontStyle(attrObj);
            SetNullFontStyle(attrObj);
            SetHorizontalTextAlignment(attrObj);
            SetVerticalTextAlignment(attrObj);
            SetHorizontalAlignment(attrObj);
            SetVerticalAlignment(attrObj);
        }
    }

    protected float[] SetScaleAbsolute(NamedNodeMap attrObj) {
        String coords = parseAttribute(attrObj, SCALEABSOLUTE, null);
        if (coords == null) return null;
        String[] fCoords = coords.split(",");
        float[] padsf = new float[fCoords.length];
        for (int y = 0; y < fCoords.length; y++) {
            padsf[y] = Float.parseFloat(fCoords[y]);
        }
        return padsf;
    }

    protected float[] SetScalePercent(NamedNodeMap attrObj) {
        String coords = parseAttribute(attrObj, SCALEPERCENT, null);
        if (coords == null) return null;
        String[] fCoords = coords.split(",");
        float[] padsf = new float[fCoords.length];
        for (int y = 0; y < fCoords.length; y++) {
            padsf[y] = Float.parseFloat(fCoords[y]);
        }
        return padsf;
    }

    protected float[] SetCoordinates(NamedNodeMap attrObj) {
        String coords = parseAttribute(attrObj, COORDINATES, "0,0");
        String[] fCoords = coords.split(",");
        float[] padsf = new float[fCoords.length];
        for (int y = 0; y < fCoords.length; y++) {
            padsf[y] = Float.parseFloat(fCoords[y]);
        }
        return padsf;
    }

    protected void SetMonthFormat(NamedNodeMap attrObj) {
        String format = parseAttribute(attrObj, MONTH_FORMAT_ATT_NAME, "long");
        if (format.equalsIgnoreCase("long")) {
            monthFormat = 2;
        }
        if (format.equalsIgnoreCase("short")) {
            monthFormat = 1;
        }
        if (format.equalsIgnoreCase("digit")) {
            monthFormat = 3;
        }
    }

    protected void SetFontStyle(NamedNodeMap attrObj) {
        String style = parseAttribute(attrObj, FONT_STYLE_ATT_NAME, "");
        if (style.equalsIgnoreCase("underline")) {
            fontStyle = Font.UNDERLINE;
        }
        if (style.equalsIgnoreCase("striked")) {
            fontStyle = Font.STRIKETHRU;
        }
        if (style.equalsIgnoreCase("normal")) {
            fontStyle = Font.NORMAL;
        }
        if (style.equalsIgnoreCase("italic")) {
            fontStyle = Font.ITALIC;
        }
        if (style.equalsIgnoreCase("bold")) {
            fontStyle = Font.BOLD;
        }
        if (style.equalsIgnoreCase("bolditalic")) {
            fontStyle = Font.BOLDITALIC;
        }
        if (style.equalsIgnoreCase("default")) {
            fontStyle = Font.DEFAULTSIZE;
        }
        if (style.equalsIgnoreCase("undefined")) {
            fontStyle = Font.UNDEFINED;
        }
    }

    protected void SetNullFontStyle(NamedNodeMap attrObj) {
        String style = parseAttribute(attrObj, NULL_FONT_STYLE_ATT_NAME, "");
        if (style.equalsIgnoreCase("underline")) {
            nullFontStyle = Font.UNDERLINE;
        }
        if (style.equalsIgnoreCase("striked")) {
            nullFontStyle = Font.STRIKETHRU;
        }
        if (style.equalsIgnoreCase("normal")) {
            nullFontStyle = Font.NORMAL;
        }
        if (style.equalsIgnoreCase("italic")) {
            nullFontStyle = Font.ITALIC;
        }
        if (style.equalsIgnoreCase("bold")) {
            nullFontStyle = Font.BOLD;
        }
        if (style.equalsIgnoreCase("bolditalic")) {
            nullFontStyle = Font.BOLDITALIC;
        }
        if (style.equalsIgnoreCase("default")) {
            nullFontStyle = Font.DEFAULTSIZE;
        }
        if (style.equalsIgnoreCase("undefined")) {
            nullFontStyle = Font.UNDEFINED;
        }
    }

    protected void SetVerticalAlignment(NamedNodeMap attrObj) {
        String valign = parseAttribute(attrObj, VERTICAL_ALIGNMENT_ATT_NAME, "top");
        if (valign.equalsIgnoreCase("bottom")) {
            verticalAlignment = Element.ALIGN_BOTTOM;
        }
        if (valign.equalsIgnoreCase("top")) {
            verticalAlignment = Element.ALIGN_TOP;
        }
        if (valign.equalsIgnoreCase("middle")) {
            verticalAlignment = Element.ALIGN_MIDDLE;
        }
        if (valign.equalsIgnoreCase("baseline")) {
            verticalAlignment = Element.ALIGN_BASELINE;
        }
    }

    protected void SetHorizontalAlignment(NamedNodeMap attrObj) {
        String halign = parseAttribute(attrObj, HORIZONTAL_ALIGNMENT_ATT_NAME, "center");
        if (halign.equalsIgnoreCase("right")) {
            horizontalAlignment = Element.ALIGN_RIGHT;
        }
        if (halign.equalsIgnoreCase("left")) {
            horizontalAlignment = Element.ALIGN_LEFT;
        }
        if (halign.equalsIgnoreCase("center")) {
            horizontalAlignment = Element.ALIGN_CENTER;
        }
        if (halign.equalsIgnoreCase("justify")) {
            horizontalAlignment = Element.ALIGN_JUSTIFIED;
        }
        if (halign.equalsIgnoreCase("justifyall")) {
            horizontalAlignment = Element.ALIGN_JUSTIFIED_ALL;
        }
    }

    protected void SetVerticalTextAlignment(NamedNodeMap attrObj) {
        String vtextalign = parseAttribute(attrObj, VERTICAL_TEXT_ALIGNMENT_ATT_NAME, "top");
        if (vtextalign.equalsIgnoreCase("bottom")) {
            verticalTextAlignment = Element.ALIGN_BOTTOM;
        }
        if (vtextalign.equalsIgnoreCase("top")) {
            verticalTextAlignment = Element.ALIGN_TOP;
        }
        if (vtextalign.equalsIgnoreCase("middle")) {
            verticalTextAlignment = Element.ALIGN_MIDDLE;
        }
        if (vtextalign.equalsIgnoreCase("baseline")) {
            verticalTextAlignment = Element.ALIGN_BASELINE;
        }
    }

    protected void SetHorizontalTextAlignment(NamedNodeMap attrObj) {
        String htextalign = parseAttribute(attrObj, HORIZONTAL_TEXT_ALIGNMENT_ATT_NAME, "");
        if (htextalign.equalsIgnoreCase("right")) {
            horizontalTextAlignment = Element.ALIGN_RIGHT;
        }
        if (htextalign.equalsIgnoreCase("left")) {
            horizontalTextAlignment = Element.ALIGN_LEFT;
        }
        if (htextalign.equalsIgnoreCase("center")) {
            horizontalTextAlignment = Element.ALIGN_CENTER;
        }
        if (htextalign.equalsIgnoreCase("justify")) {
            horizontalTextAlignment = Element.ALIGN_JUSTIFIED;
        }
        if (htextalign.equalsIgnoreCase("justifyall")) {
            horizontalTextAlignment = Element.ALIGN_JUSTIFIED_ALL;
        }
    }

    protected void LoadItems(Node node, HashMap<String, ReportBaseFont> fonts, BaseReportObject pParent, PropertyGetter pGetter) throws ParseException {
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node item = nodes.item(i);
            String nodeName = item.getNodeName();
            if (nodeName.equals("items")) {
                LoadItems(item, fonts, pParent, pGetter);
            } else {
                switch (nodeName) {
                    case table:
                        items.add(new ReportTable(item, fonts, pParent, pGetter));
                        break;
                    case paragraph:
                        items.add(new ReportParagraph(item, fonts, pParent, pGetter));
                        break;
                    case chunk:
                        items.add(new ReportChunk(item, fonts, pParent, pGetter));
                        break;
                    case moneychunk:
                        items.add(new ReportMoneyChunk(item, fonts, pParent, pGetter));
                        break;
                    case phrase:
                        items.add(new ReportPhrase(item, fonts, pParent, pGetter));
                        break;
                    case cell:
                        items.add(new ReportCell(item, fonts, pParent, pGetter));
                        break;
                    /*case repeatingrow:
                        items.add(new ReportRepeatingRow(item, fonts, pParent, pGetter, _doc));
                        break;*/
                    case wordsplitter:
                        items.add(new ReportWordSplitter(item, fonts, pParent, pGetter));
                        break;
                    case dateparagraph:
                        items.add(new ReportDate(item, fonts, pParent, pGetter));
                        break;
                    case day:
                        items.add(new ReportDateDay(item, fonts, pParent, pGetter));
                        break;
                    case month:
                        items.add(new ReportDateMonth(item, fonts, pParent, pGetter));
                        break;
                    case year:
                        items.add(new ReportDateYear(item, fonts, pParent, pGetter));
                        break;
                    case image:
                        items.add(new ReportImage(item, fonts, pParent, pGetter));
                        break;
                    case newpage:
                        items.add(new ReportNewPage());
                        break;
                }
            }
        }
    }

    public float[] parsePaddings(String paddings) {
        if (paddings.length() > 0) {
            String[] pads = paddings.split(",");
            float[] padsf = new float[pads.length];
            for (int y = 0; y < pads.length; y++) {
                padsf[y] = Float.parseFloat(pads[y]);
            }
            return padsf;
        }
        return null;
    }

    protected static final String MIN_FREE_SPACE_AFTER="minfreespaceafter";
    protected static final String AGGR_COL="aggrcol";
    protected static final String AGGR_FUNC="aggrfunc";
    protected static final String PAGE_NUM_TYPE="pagenumtype";
    protected static final String PAGE_HEADER="pageHeader";
    protected static final String PAGE_TEXT="pageText";
    protected static final String TEXT_CASE="textcase";
    protected static final String CHARACTER_SPACING="charspacing";
    protected static final String CHUNK_INDEX="index";
    protected static final String NEGATIVE_EMBRACE="negativeembrace";
    protected static final String BORDER_STYLE_ATT="borderstyle";
    protected static final String DECIMAL_SEPARATOR="decseparator";
    protected static final String REPLICATE_HEADER="replicateheader";
    protected static final String REPLICATE_FOOTER="replicatefooter";
    protected static final String REPROW_OTHER_PAGE_HEIGHT = "reprowotherpageheight";
    protected static final String DEFAULT_NULL_VALUE = "defaultnullvalue";
    protected static final String REPROW_FPAGE_ROWS = "reprowfpagerows";
    protected static final String REPROW_OTHER_PAGE_ROWS = "reprowotherpagerows";
    protected static final String STRING_FORMAT_ATT_NAME = "stringformat";
    protected static final String FONT_STYLE_ATT_NAME = "fontstyle";
    protected static final String NULL_FONT_STYLE_ATT_NAME = "nullfontstyle";
    protected static final String FONT_ATT_NAME = "font";
    protected static final String FONT_WEIGHT_ATT_NAME = "fontweight";
    protected static final String LEADING_ATT_NAME = "leading";
    protected static final String WIDTH_PERCENTAGE_ATT_NAME = "widthpercentage";
    protected static final String WIDTH_CELLS_PERCENTAGE_ATT_NAME = "widthcellspercentage";
    protected static final String COLUMNS_ATT_NAME = "columns";
    protected static final String BORDER_WIDTH_ATT_NAME = "borderwidth";
    protected static final String BORDER_WIDTH_LEFT_ATT_NAME = "borderwidthleft";
    protected static final String BORDER_WIDTH_RIGHT_ATT_NAME = "borderwidthright";
    protected static final String BORDER_WIDTH_TOP_ATT_NAME = "borderwidthtop";
    protected static final String BORDER_WIDTH_BOTTOM_ATT_NAME = "borderwidthbottom";
    protected static final String PADDINGS_ATT_NAME = "paddings";
    protected static final String CELL_MODE_ATT_NAME = "mode";
    protected static final String HORIZONTAL_TEXT_ALIGNMENT_ATT_NAME = "htextalign";
    protected static final String VERTICAL_TEXT_ALIGNMENT_ATT_NAME = "vtextalign";
    protected static final String HORIZONTAL_ALIGNMENT_ATT_NAME = "halign";
    protected static final String VERTICAL_ALIGNMENT_ATT_NAME = "valign";
    protected static final String SPACING_AFTER_ATT_NAME = "spacingafter";
    protected static final String SPACING_BEFORE_ATT_NAME = "spacingbefore";
    protected static final String USE_BORDER_PADDINGS_ATT_NAME = "useborderpaddings";
    protected static final String PROPERTY_ATT_NAME = "property";
    protected static final String PAGE_NAME_ATT_NAME = "pagename";
    protected static final String COL_SPAN_ATT_NAME = "colspan";
    protected static final String ROW_SPAN_ATT_NAME = "rowspan";
    protected static final String FIXED_HEIGHT_ATT_NAME = "fixedheight";
    protected static final String MINIMUM_HEIGHT_ATT_NAME = "minimumheight";
    protected static final String TOTAL_WIDTH_ATT_NAME = "totalwidth";
    protected static final String CELL_HEIGHT_ATT_NAME = "cellheight";
    protected static final String WORD_SPLITTER_ALIGN_ATT_NAME = "wordalign";
    protected static final String MONTH_FORMAT_ATT_NAME = "format";
    protected static final String DATE_FORMAT_ATT_NAME = "dateformat";
    protected static final String TO_DATE_FORMAT_ATT_NAME = "todateformat";
    protected static final String FIRST_SYMBOLS_YEAR_ATT_NAME = "first";
    protected static final String LAST_SYMBOLS_YEAR_ATT_NAME = "last";
    protected static final String PARAGRAPH_INDENTATION_LEFT_ATT_NAME = "indentationleft";
    protected static final String PARAGRAPH_INDENTATION_RIGHT_ATT_NAME = "indentationright";
    protected static final String PARAGRAPH_FIRSTLINE_INDENT_ATT_NAME = "firstlineindentation";
    protected static final String CUSTOM_TEXT_RIGHT_ATT_NAME = "customtext";
    protected static final String STOP_INHERIT_ATT_NAME = "stopinherit";
    protected static final String PROPERTY_MODE_ATT_NAME = "propertymode";
    protected static final String BACKGROUNDCOLOR = "bgcolor";
    protected static final String BORDERCOLOR = "bordercolor";
    protected static final String BORDERCOLORTOP = "bordercolor";
    protected static final String BORDERCOLORBOTTOM = "bordercolor";
    protected static final String BORDERCOLORRIGHT = "bordercolor";
    protected static final String BORDERCOLORLEFT = "bordercolor";
    protected static final String TEXTCOLOR = "textcolor";
    protected static final String FILENAME = "filename";
    protected static final String FILEFOLDER = "filefolder";
    protected static final String FILETYPE = "filetype";
    protected static final String POSITION = "position";
    protected static final String COORDINATES = "coordinates";
    protected static final String SCALEPERCENT = "scalepercent";
    protected static final String SCALEABSOLUTE = "scaleabsolute";
    protected static final String BGIMAGE = "bgimage";
    protected static final String KEEPTOGETHER = "keeptogether";

    protected Float reprowotherpageheight;
    protected Float spacingAfter;
    protected Float spacingBefore;
    protected Float widthpercentage;
    protected Float[] widthcellspercentage;
    protected Float paddingLeft;
    protected Float paddingRight;
    protected Float paddingTop;
    protected Float paddingBottom;
    protected Float fontWeight;
    protected Float fixedHeight;
    protected Float minimumHeight;
    protected Float borderWidth;
    protected Float borderWidthLeft;
    protected Float borderWidthRight;
    protected Float borderWidthTop;
    protected Float borderWidthBottom;
    protected Float leading;
    protected Float totalWidth;
    protected Float cellHeight;
    protected Float indentationLeft;
    protected Float indentationRight;
    protected Float firstLineIndentation;
    protected float[] coordinates;
    protected float[] scalepercent;
    protected float[] scaleabsolute;

    protected Integer aggrCol;
    protected Integer reprowfpagerows;
    protected Integer reprowotherpagerows;
    protected Integer fontStyle;
    protected Integer nullFontStyle;
    protected Integer columns;
    protected Integer colSpan;
    protected Integer rowSpan;
    protected Integer horizontalAlignment;
    protected Integer verticalAlignment;
    protected Integer horizontalTextAlignment;
    protected Integer verticalTextAlignment;
    protected Integer monthFormat;
    protected Integer firstSymbols;
    protected Integer lastSymbols;
    protected Integer charspacing;
    protected Integer minFreeSpaceAfter;

    protected String aggrFunc;
    protected String pageNumType;
    protected String pageHeader;
    protected String pageText;
    protected String textcase;
    protected String index;
    protected String borderstyle;
    protected String decseparator;
    protected String defaultnullvalue;
    protected String stringformat;
    protected String bgimage;
    protected String position;
    protected String fileType;
    protected String fileFolder;
    protected String fileName;
    protected String textColor;
    protected String bgColor;
    protected String borderColor;
    protected String borderColorTop;
    protected String borderColorBottom;
    protected String borderColorRight;
    protected String borderColorLeft;
    protected String toDateFormat;
    protected String dateFormat;
    protected String propertyMode;
    protected String customtext;
    protected String wordalign;
    protected String text;
    protected String fontName;
    protected String propertyName;
    protected String cellMode;
    protected String pageName;

    protected ArrayList<BaseReportObject> items = new ArrayList<BaseReportObject>();

    protected Boolean useBorderPadding;
    protected Boolean stopInherit;
    protected Boolean keepTogether;
    protected Boolean replicateHeader;
    protected Boolean replicateFooter;
    protected Boolean negativeEmbrace;

    protected float[] getScalePercent() {
        if (scalepercent == null) return null;

        if (scalepercent.length == 2) return scalepercent;

        return null;
    }

    protected float[] getScaleAbsolute() {
        if (scaleabsolute == null) return null;
        if (scaleabsolute.length == 2) return scaleabsolute;

        return null;
    }

    protected float[] getCoordinates() {
        if (coordinates.length == 2) return coordinates;

        return null;
    }


    protected float getIndentationLeft() {
        if (indentationLeft >= 0) return indentationLeft;
        else if (parent != null) return parent.getIndentationLeft();

        return -1f;
    }

    protected float getIndentationRight() {
        if (indentationRight >= 0) return indentationRight;
        else if (parent != null) return parent.getIndentationRight();

        return -1f;
    }

    protected float getFirstLineIndentation() {
        if (firstLineIndentation >= 0) return firstLineIndentation;
        else if (parent != null) return parent.getFirstLineIndentation();

        return -1f;
    }

    /*
    Float attributes
     */
    protected Float getSpacingAfter() {
        if (spacingAfter >= 0) return spacingAfter;
        else if (parent != null) return parent.getSpacingAfter();

        return -1f;
    }

    protected Float getSpacingBefore() {
        if (spacingBefore >= 0) return spacingBefore;
        else if (parent != null) return parent.getSpacingBefore();

        return -1f;
    }

    protected Float getTotalWidth() {
        if (totalWidth >= 0) return totalWidth;
        else if (parent != null) return parent.getTotalWidth();

        return 100f;
    }

    protected Float getWidthPercentage() {
        if (widthpercentage >= 0) return widthpercentage;
        else if (parent != null) return parent.getWidthPercentage();

        return 100f;
    }

    protected float[] getWidthCellsPercentage() {
        if (widthcellspercentage != null) {
            if (widthcellspercentage.length > 0) {
                float[] f = new float[widthcellspercentage.length];
                for (int y = 0; y < widthcellspercentage.length; y++) {
                    f[y] = widthcellspercentage[y];
                }
                return f;
            }
        }

        return null;
    }

    protected Float getFixedHeight() {
        if (fixedHeight >= 0) return fixedHeight;
        else if (parent != null) return parent.getFixedHeight();

        return -1f;
    }

    protected float getMinimumHeight() {
        if (minimumHeight >= 0) return minimumHeight;
        else if (parent != null) return parent.getMinimumHeight();

        return -1f;
    }

    protected Float getPaddingLeft() {
        if (paddingLeft >= 0) return paddingLeft;
        else if (parent != null) return parent.getPaddingLeft();

        return -1f;
    }

    protected Float getPaddingRight() {
        if (paddingRight >= 0) return paddingRight;
        else if (parent != null) return parent.getPaddingRight();

        return -1f;
    }

    protected Float getPaddingTop() {
        if (paddingTop >= 0) return paddingTop;
        else if (parent != null) return parent.getPaddingTop();

        return -1f;
    }

    protected Float getPaddingBottom() {
        if (paddingBottom >= 0) return paddingBottom;
        else if (parent != null) return parent.getPaddingBottom();

        return -1f;
    }

    protected float[] getPaddings() {
        float[] pads = new float[4];
        pads[0] = getPaddingLeft();
        pads[1] = getPaddingRight();
        pads[2] = getPaddingTop();
        pads[3] = getPaddingBottom();
        return pads;
    }

    protected Float getBorderWidth() {
        if (borderWidth >= 0) return borderWidth;
        else if (parent != null) return parent.getBorderWidth();

        return -1f;
    }

    protected Float getBorderWidthLeft() {
        if (borderWidthLeft >= 0) return borderWidthLeft;
        return -1f;
    }

    protected Float getBorderWidthRight() {
        if (borderWidthRight >= 0) return borderWidthRight;
        return -1f;
    }

    protected Float getBorderWidthTop() {
        if (borderWidthTop >= 0) return borderWidthTop;
        return -1f;
    }

    protected Float getBorderWidthBottom() {
        if (borderWidthBottom >= 0) return borderWidthBottom;
        return -1f;
    }

    protected Float getLeading() {
        if (leading >= 0) return leading;
        else if (parent != null) return parent.getLeading();

        return -1f;
    }

    protected String getAggrFunc() {
        if (aggrFunc != null)
            return aggrFunc;
        return null;
    }

    protected String getPageNumType() {
        if (pageNumType != null)
            return pageNumType;
        return null;
    }

    protected String getPageText() {
        if (pageText != null) {
            return pageText;
        }
        return null;
    }

    protected String getFontName() {
        if (fontName != null) {
            return fontName;
        } else if (parent != null) {
            return parent.getFontName();
        }
        return null;
    }

    protected String getBorderStyle() {
        if (borderstyle != null) {
            return borderstyle;
        }
        return null;
    }

    protected Float getFontWeight() {
        if (fontWeight == null) return null;
        if (fontWeight > 0) {
            return fontWeight;
        } else if (parent != null) {
            return parent.getFontWeight();
        }
        return null;
    }

    protected Float getCellHeight() {
        return cellHeight;
    }
    /*
    Integer attributes
     */

    protected Float getRepRowOtherPageHeight(){
        if(reprowotherpageheight!=null) return reprowotherpageheight;
        return 0f;
    }

    protected Float getVerticalSize() throws DocumentException, ParseException, IOException {
        return null;
    }

    protected String getPageHeader () {
        if(pageHeader != null) return pageHeader;
        return null;
    }

    protected String getDefaultNullValue(){
        if(defaultnullvalue!=null) return defaultnullvalue;
        else if(parent != null ) return parent.getDefaultNullValue();
        return null;
    }

    protected String getStringformat(){
        if(stringformat!=null) return stringformat;

        if(parent != null && parent.stringformat!=null){
            return parent.stringformat;
        }
        return null;
    }

    protected String getDecimalSeparator(){
        if(decseparator!=null) return decseparator;
        return null;
    }


    protected String getTextCase() {
        if (textcase != null) return textcase;
        else if (parent != null) return parent.getTextCase();

        return null;
    }

    protected Integer getMinFreeSpaceAfter() {
        if(minFreeSpaceAfter!=null) return  minFreeSpaceAfter;
        return 0;
    }

    protected int getRepRowFPageRows(){
        if(reprowfpagerows!=null) return reprowfpagerows;
        return 0;
    }

    protected int getRepRowOtherPageRows() {
        if (reprowotherpagerows != null) return reprowotherpagerows;
        return 0;
    }

    protected Integer getColumns() {
        if (columns > 0) return columns;
        return -1;
    }

    protected Integer getAggrCol() {
        if (aggrCol != null && aggrCol > 0) return aggrCol;
        return null;
    }

    protected Integer getCharspacing() {
        if (charspacing > 0) return charspacing;
        else if (parent != null) return parent.getCharspacing();

        return -1;
    }

    protected Integer getColSpan() {
        if (colSpan > 0) return colSpan;
        return -1;
    }

    protected Integer getRowSpan() {
        if (rowSpan > 0) return rowSpan;
        return -1;
    }

    protected Integer getHorizontalAlignment() {
        if (horizontalAlignment != null) return horizontalAlignment;
        else if (parent != null) return parent.getHorizontalAlignment();

        return Element.ALIGN_CENTER;
    }

    protected Integer getVerticalAlignment() {
        if (verticalAlignment != null) return verticalAlignment;
        else if (parent != null) return parent.getVerticalAlignment();

        return Element.ALIGN_TOP;
    }

    protected Integer getHorizontalTextAlignment() {
        if (horizontalTextAlignment != null) return horizontalTextAlignment;
        else if (parent != null) return parent.getHorizontalTextAlignment();

        return Element.ALIGN_CENTER;
    }

    protected Integer getVerticalTextAlignment() {
        if (verticalTextAlignment != null) return verticalTextAlignment;
        else if (parent != null) return parent.getVerticalTextAlignment();

        return Element.ALIGN_TOP;
    }

    protected int getMonthFormat() {
        if (monthFormat != null) return monthFormat;

        return 2;
    }


    protected int getLastSymbolsNum() {
        if (lastSymbols != null) {
            return lastSymbols;
        }
        return -1;
    }


    protected String getCellMode() {
        if (cellMode != null) {
            return cellMode;
        } else if (parent != null) {
            return parent.getCellMode();
        }
        return COMPOSITE;
    }

    protected String getPosition() {
        return position;
    }

    protected String getPageName() {
        if (pageName != null) {
            return pageName;
        } else if (parent != null) {
            return parent.getPageName();
        }
        return null;
    }

    protected String getPropertyName() {
        if (propertyName != null) {
            return propertyName;
        }

        if (parent != null) {
            return parent.getPropertyName();
        }

        return null;
    }

    protected String getText() {
        if (text != null) return text;
        return null;
    }

    protected String getWordAlign() {
        if (wordalign != null) return wordalign;
        return null;
    }

    protected String getChunkIndex() {
        if (index != null)
            return index;

        return "normal";
    }

    protected Boolean getNegativeEmbrace() {
        if (negativeEmbrace != null) {
            return negativeEmbrace;
        } else if (parent != null && parent.getNegativeEmbrace() != null) {
            return parent.getNegativeEmbrace();
        }
        return false;
    }

    protected Boolean getUseBorderPadding() {
        if (useBorderPadding != null) {
            return useBorderPadding;
        } else if (parent != null) {
            return parent.getUseBorderPadding();
        }
        return null;
    }

    protected Boolean getKeepTogether() {
        return keepTogether;
    }

    protected Boolean getStopInherit() {
        return stopInherit;
    }

    protected Boolean getReplicateHeader() {
        if(replicateHeader != null) return replicateHeader;
        return false;
    }

    protected Boolean getReplicateFooter() {
        if(replicateFooter != null) return replicateFooter;
        return false;
    }

    protected String getCustomText() {
        if (customtext != null) return customtext;
        return null;
    }

    protected String getDateFormat() {
        if (dateFormat != null) return dateFormat;
        return null;
    }

    protected String getToDateFormat() {
        if (toDateFormat != null){

            return toDateFormat;
        }

        return null;
    }

    protected String getPropertyMode() {
        if (propertyMode != null) return propertyMode;

        return null;
    }

    protected String getFileName() {
        if (fileName != null) return fileName;

        return null;
    }

    protected String getFileFolder() {
        if (fileFolder != null) return fileFolder;

        return null;
    }

    protected String getFileType() {
        if (fileType != null) return fileType;

        return null;
    }

    protected String getBGImage() {
        if (bgimage != null) return bgimage;

        return null;
    }

    public static String parseAttribute(NamedNodeMap attrs, String attrName, String defaultValue) {
        if (attrs == null) return defaultValue;
        Node attrObj = attrs.getNamedItem(attrName);
        if (attrObj == null) return defaultValue;
        return attrObj.getTextContent();
    }


    public void SetAttribute(NamedNodeMap attrs, String attName, String attValue) {
        if (attrs == null) return;
        Node attrObj = attrs.getNamedItem(attName);
        if (attrObj != null) {
            attrObj.setNodeValue(attValue);
        }
    }

    protected Integer getFontStyle() {
        if (fontStyle != null) return fontStyle;
        if (parent != null) return parent.getFontStyle();

        return null;
    }

    protected Integer getNullFontStyle() {
        if (nullFontStyle != null) return nullFontStyle;
        if (parent != null) return parent.getNullFontStyle();

        return null;
    }

    public Font getFont() {
        String f = getFontName();
        Float fw = getFontWeight();
        Integer style = getFontStyle();
        if (f != null && fw > 0 && style == null) {
            return _fonts.get(f).getCustomFont(fw);
        }
        if (f != null && fw > 0 && style != null) {
            return _fonts.get(f).getCustomFont(fw, style);
        }
        return null;
    }

    public Font getNullFont() {
        String f = getFontName();
        Float fw = getFontWeight();
        Integer style = getNullFontStyle();
        if (f != null && fw > 0 && style == null) {
            return _fonts.get(f).getCustomFont(fw);
        }
        if (f != null && fw > 0 && style != null) {
            return _fonts.get(f).getCustomFont(fw, style);
        }
        return null;
    }

    public int[] getBGColor() {
        if (SplitString(bgColor) != null) return SplitString(bgColor);
        if (parent != null) return parent.getBGColor();
        return null;
    }

    public int[] getTextColor() {
        if (SplitString(textColor) != null) return SplitString(textColor);
        if (parent != null) return parent.getTextColor();
        return null;
    }

    public int[] getBorderColor() {
        if (SplitString(borderColor) != null) return SplitString(borderColor);
        if (parent != null) return parent.getBorderColor();
        return null;
    }

    public int[] getBorderColorTop() {
        return SplitString(borderColorTop);
    }

    public int[] getBorderColorBottom() {
        return SplitString(borderColorBottom);
    }

    public int[] getBorderColorLeft() {
        return SplitString(borderColorLeft);
    }

    public int[] getBorderColorRight() {
        return SplitString(borderColorRight);
    }

    private int[] SplitString(String param) {
        if (param == null) return null;
        String[] bgColorArr = param.split(",");
        if (bgColorArr.length != 3) return null;

        int[] ret = new int[3];
        ret[0] = Integer.parseInt(bgColorArr[0]);
        ret[1] = Integer.parseInt(bgColorArr[1]);
        ret[2] = Integer.parseInt(bgColorArr[2]);
        return ret;
    }

    protected String ConvertPropertyToSpecificDateFormat(String propertyValue) {

        Date s = propertyGetter.GetPropertyStringAsDate(propertyValue, getDateFormat());
        String defaultNullValue = getDefaultNullValue();
        String format = getToDateFormat();

        return DateUtils.getFormattedDate(defaultNullValue, s, format);
    }
}
