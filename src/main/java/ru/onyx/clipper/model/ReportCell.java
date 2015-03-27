package ru.onyx.clipper.model;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import org.w3c.dom.Node;
import ru.onyx.clipper.data.PropertyGetter;
import ru.onyx.clipper.utils.ReportStrUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * User: Alex
 * Date: 15.03.12
 * Time: 11:24
 */
public class ReportCell extends BaseReportObject {

	class DashedCell implements PdfPCellEvent {
		public void cellLayout(PdfPCell cell, Rectangle rect,
							   PdfContentByte[] canvas) {
			PdfContentByte cb = canvas[PdfPTable.LINECANVAS];
			cb.setLineCap(PdfContentByte.LINE_CAP_PROJECTING_SQUARE);
			cb.setLineDash(new float[]{0.125f, 8.0f}, 5.0f);
			cb.stroke();
		}
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setCustomText(String text) {
		this.customtext = text;
	}

	//---------------------------------------------------

	public ReportCell(float pfixedHeight, String text, int vtextalign, int htextalign, String pFontName, Float fontW, float borderwidth, float pleading, float[] paddings, Boolean usebPaddings, int[] bgColorp, int[] borderColorp, PropertyGetter pGetter, HashMap<String, ReportBaseFont> pFonts, Report report) {
		_fonts = pFonts;
		propertyGetter = pGetter;
		//First we unset all properties...
		SetParameters();
		// ...then - set our custom
		this.useBorderPadding = usebPaddings;
		this.cellMode = "text";
		this.text = text;
		this.borderWidth = borderwidth;
		this.leading = pleading;
		this.fixedHeight = pfixedHeight;
		this.horizontalTextAlignment = htextalign;
		this.verticalTextAlignment = vtextalign;
		this.horizontalAlignment = htextalign;
		this.fontName = pFontName;
		this.fontWeight = fontW;
		this._fonts = pFonts;
		this.bgColor = Arrays.toString(bgColorp).replace("[", "").replace("]", "").replace(", ", ",");
		this.borderColor = Arrays.toString(borderColorp).replace("[", "").replace("]", "").replace(", ", ",");
        this.report=report;
	}

	/**
	 * Main constructor
	 */
	public ReportCell(Node node, HashMap<String, ReportBaseFont> fonts, BaseReportObject pParent, PropertyGetter pGetter, Report report) throws ParseException, IOException, DocumentException {
		_fonts = fonts;
		parent = pParent;
		propertyGetter = pGetter;
        this.report=report;
		Load(node);
		LoadItems(node, fonts, this, pGetter);
		if (pParent != null && pParent.getPageNameRT() != null) {
			setPageNameRT(pParent.getPageNameRT());
		}

	}

	public void SetParameters() {
		this.stopInherit = false;
		this.borderWidth = -1f;
		this.borderWidthBottom = -1f;
		this.borderWidthLeft = -1f;
		this.borderWidthRight = -1f;
		this.borderWidthTop = -1f;
		this.fixedHeight = -1f;
		this.minimumHeight = -1f;
		this.paddingBottom = -1f;
		this.paddingLeft = -1f;
		this.paddingRight = -1f;
		this.paddingTop = -1f;
		this.useBorderPadding = false;
		this.colSpan = -1;
		this.rowSpan = -1;
		this.indentationLeft = -1f;
		this.indentationRight = -1f;
		this.firstLineIndentation = -1f;
		this.spacingAfter = -1f;
		this.spacingBefore = -1f;
		this.bgColor = null;
		this.borderColor = null;

	}


	public PdfPCell getPdfObject() throws DocumentException, ParseException, IOException {
		String celltext = "";
		Font NullF = null;
		if (getText() != null) celltext = getText();


		if (getPropertyName() != null && customtext == null) {
			celltext = propertyGetter.GetProperty(getPropertyName());
			if (celltext == null) {
				NullF = getNullFont();
				celltext = getDefaultNullValue();
			}

			if (getPropertyExtract() != null) {
				String delimiters;
				if (getExtractDelimiter() != null) {
					delimiters = getExtractDelimiter();
				} else {
					delimiters = ",.";
				}
				StringTokenizer st = new StringTokenizer(celltext, delimiters, true);
				int extIndex = Integer.parseInt(getPropertyExtract());
				ArrayList<String> extData = new ArrayList<>();
				boolean dataFlag = false;
				while (st.hasMoreTokens()) {
					String key = st.nextToken();
					if (key.equals(",") && !dataFlag) {
						extData.add("");
					} else if (key.equals(",") || key.equals(".") && dataFlag) {
						dataFlag = false;
					} else if (key.equals(".") && !dataFlag) {
						extData.add("");
					} else if (key.equals(".") && dataFlag) {
						dataFlag = false;
					} else {
						extData.add(key);
						dataFlag = true;
					}
				}
				if (extIndex < extData.size()) {
					celltext = extData.get(extIndex);
				} else if (getDefaultNullValue() != null) {
					celltext = defaultnullvalue;
				} else {
					celltext = "";
				}
			}


			if (getDateFormat() != null && getToDateFormat() != null) {
				try {
					celltext = ConvertPropertyToSpecificDateFormat(celltext);
				} catch (Exception e) {
					celltext = "Formatting failed";
				}
			}
		}


		if (getPropertyName() != null && !getPropertyName().contains("$") && parent.getPageNameRT() != null) {
			celltext = propertyGetter.GetProperty(parent.getPageNameRT() + getPropertyName());
		}



		if (getStringformat() != null && celltext != null && !celltext.isEmpty() && !celltext.equals("-")) {
			try {
				if (getLocaleDel() != null && getLocaleDel().equals(".")) {
					celltext = String.format(Locale.ENGLISH, getStringformat(), Double.parseDouble(celltext));
				} else {
					if (getStringformat().equals("tenth")) {
						celltext = celltext.substring(celltext.indexOf(".") + 1);
					} else if (getStringformat().equals("integral")) {
						celltext = String.format(new Locale("ru"), "%,6.0f", Double.parseDouble(celltext.substring(0, celltext.indexOf("."))));
					} else {
						Locale locale = new Locale("ru");
						celltext = String.format(locale, getStringformat(), Double.parseDouble(celltext));
					}
				}
			} catch (NumberFormatException e) {
				celltext = "";
			}
		}

		if (getDecimalSeparator() != null) {
			celltext = ReportStrUtils.replaceDecSeparator(celltext, getDecimalSeparator());
		}

		if (customtext != null) {
			celltext = customtext;
			if (getDateFormat() != null && getToDateFormat() != null) {
				celltext = ConvertPropertyToSpecificDateFormat(customtext);
			}

			if (getStringformat() != null && celltext != null && !celltext.isEmpty() && !celltext.equals("-")) {
				try {
					if (getLocaleDel() != null && getLocaleDel().equals(".")) {
						celltext = String.format(Locale.ENGLISH, getStringformat(), Double.parseDouble(celltext));
					} else {
						if (getStringformat().equals("tenth")) {
							celltext = celltext.substring(celltext.indexOf(".") + 1);
						} else if (getStringformat().equals("integral")) {
							celltext = String.format(new Locale("ru"), "%,6.0f", Double.parseDouble(celltext.substring(0, celltext.indexOf("."))));
						} else {
							Locale locale = new Locale("ru");
							celltext = String.format(locale, getStringformat(), Double.parseDouble(celltext));
						}
					}
				} catch (NumberFormatException e) {
					celltext = "";
				}
			}

			if (getDecimalSeparator() != null) {
				celltext = ReportStrUtils.replaceDecSeparator(celltext, getDecimalSeparator());
			}
		}

		if (getIfZero() != null && celltext != null) {
			if (celltext.trim().equals("0,00") || celltext.trim().equals("0.00") || celltext.trim().equals("0")) {
				celltext = getIfZero();
			}
			if (celltext.equals("null")) {
				celltext = null;
			}
		}

		if (getNegativeEmbrace()) {
			celltext = ReportStrUtils.embraceNegativeValue(celltext);
		}

		if (getTextCase() != null) {
			if (getTextCase().equals("upper")) celltext = celltext.toUpperCase();
			if (getTextCase().equals("lower")) celltext = celltext.toLowerCase();
		}

		if(celltext != null) {
			if (celltext.equalsIgnoreCase("true")) {
				celltext = "\uf0FE";
			}
			if (celltext.equalsIgnoreCase("false")) {
				celltext = "\uf0A8";
			}
		}

		if (getLastTableRowCount()) {
			celltext = String.valueOf(Report.getLastTableRowCount());
		}

		PdfPCell cell;

		if (getCellMode().equalsIgnoreCase("text")) {
			Paragraph par = new Paragraph();
			Chunk ch;
			Font f = getFont();
			if(celltext !=null) {
				if (f != null) {
					int[] color = getTextColor();
					if (color != null) f.setColor(color[0], color[1], color[2]);
					ch = new Chunk(celltext, f);
					par = new Paragraph(ch);
				} else {
					par = new Paragraph(celltext);
				}
			}

			if (NullF != null && celltext != null) {
				int[] color = getTextColor();
				if (color != null) NullF.setColor(color[0], color[1], color[2]);
				ch = new Chunk(celltext, NullF);
				par = new Paragraph(ch);
			}

			if (getLeading() >= 0) par.setLeading(getLeading());
			par.setAlignment(getHorizontalTextAlignment());
			if (getIndentationLeft() >= 0) par.setIndentationLeft(getIndentationLeft());
			if (getIndentationRight() >= 0) par.setIndentationRight(getIndentationRight());
			if (getSpacingAfter() >= 0) par.setSpacingAfter(getSpacingAfter());
			if (getSpacingBefore() >= 0) par.setSpacingBefore(getSpacingBefore());
			if (getFirstLineIndentation() >= 0) par.setFirstLineIndent(getFirstLineIndentation());
			cell = new PdfPCell(par);
			cell.setHorizontalAlignment(getHorizontalTextAlignment());
			cell.setVerticalAlignment(getVerticalTextAlignment());
		} else if (getBGImage() != null) {

			Image img = propertyGetter.GetImage(getBGImage(), getFileFolder(), getFileType());

			float[] scaleab = getScaleAbsolute();
			if (scaleab != null)
				img.scaleToFit(scaleab[0], scaleab[1]);


			Chunk ch = new Chunk(img, 0, 0, true);

			Phrase ph = new Phrase(ch);
			ph.setLeading(0);
			// cell = new PdfPCell(ph);
			cell = new PdfPCell(img);
		} else {
			cell = new PdfPCell();
		}
		if (getBorderWidth() >= 0) cell.setBorderWidth(getBorderWidth());
		if (getBorderWidthLeft() >= 0) cell.setBorderWidthLeft(getBorderWidthLeft());
		if (getBorderWidthRight() >= 0) cell.setBorderWidthRight(getBorderWidthRight());
		if (getBorderWidthTop() >= 0) cell.setBorderWidthTop(getBorderWidthTop());
		if (getBorderWidthBottom() >= 0) cell.setBorderWidthBottom(getBorderWidthBottom());
		if (getColSpan() >= 0) cell.setColspan(getColSpan());
		if (getRowSpan() >= 0) cell.setRowspan(getRowSpan());
		if (getFixedHeight() >= 0) cell.setFixedHeight(getFixedHeight());
		if (getMinimumHeight() >= 0) cell.setMinimumHeight(getMinimumHeight());

		if (getPaddingLeft() >= 0) cell.setPaddingLeft(getPaddingLeft());
		if (getPaddingRight() >= 0) cell.setPaddingRight(getPaddingRight());
		if (getPaddingTop() >= 0) cell.setPaddingTop(getPaddingTop());
		if (getPaddingBottom() >= 0) cell.setPaddingBottom(getPaddingBottom());

		int[] borderColor = getBorderColor();
		if (borderColor != null) cell.setBorderColor(new BaseColor(borderColor[0], borderColor[1], borderColor[2]));

		int[] borderColorTop = getBorderColorTop();
		if (borderColorTop != null)
			cell.setBorderColorTop(new BaseColor(borderColorTop[0], borderColorTop[1], borderColorTop[2]));

		int[] borderColorBottom = getBorderColorBottom();
		if (borderColorBottom != null)
			cell.setBorderColorBottom(new BaseColor(borderColorBottom[0], borderColorBottom[1], borderColorBottom[2]));

		int[] borderColorLeft = getBorderColorLeft();
		if (borderColorLeft != null)
			cell.setBorderColorLeft(new BaseColor(borderColorLeft[0], borderColorLeft[1], borderColorLeft[2]));

		int[] borderColorRight = getBorderColorRight();
		if (borderColorRight != null)
			cell.setBorderColorRight(new BaseColor(borderColorRight[0], borderColorRight[1], borderColorRight[2]));

		cell.setUseBorderPadding(getUseBorderPadding());

		int[] bgColor = getBGColor();
		if (bgColor != null) cell.setBackgroundColor(new BaseColor(bgColor[0], bgColor[1], bgColor[2]));

		cell.setVerticalAlignment(getVerticalTextAlignment());

		for (int y = 0; y < items.size(); y++) {
			cell.addElement(items.get(y).getPdfObject());
		}

		if (getBorderStyle() != null && getBorderStyle().equals("dotted")) {
			DashedCell border = new DashedCell();
			cell.setCellEvent(border);
		}

		return cell;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
