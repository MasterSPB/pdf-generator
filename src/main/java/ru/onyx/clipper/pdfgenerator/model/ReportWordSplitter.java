package ru.onyx.clipper.pdfgenerator.model;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import org.w3c.dom.Node;
import ru.onyx.clipper.pdfgenerator.data.PropertyGetter;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * User: Alex
 * Date: 15.03.12
 * Time: 14:21
 */
public class ReportWordSplitter extends BaseReportObject {

	class DashedTable implements PdfPTableEvent {
		@Override
		public void tableLayout(PdfPTable table, float[][] widths,
								float[] heights, int headerRows, int rowStart,
								PdfContentByte[] canvases) {
			PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
			canvas.saveState();
			canvas.setLineCap(PdfContentByte.LINE_CAP_PROJECTING_SQUARE);
			canvas.setLineDash(new float[]{0.125f, 3.0f}, 5.0f);
			float llx = widths[0][0];
			float urx = widths[0][widths.length];
			for (int i = 0; i < heights.length; i++) {
				canvas.moveTo(llx, heights[i]);
				canvas.lineTo(urx, heights[i]);
			}
			for (int i = 0; i < widths.length; i++) {
				for (int j = 0; j < widths[i].length; j++) {
					canvas.moveTo(widths[i][j], heights[i]);
					canvas.lineTo(widths[i][j], heights[i + 1]);
				}
			}
			canvas.stroke();
			canvas.restoreState();
		}
	}

	private ArrayList<ReportCell> items = new ArrayList<ReportCell>();

	public ReportWordSplitter(Node node, HashMap<String, ReportBaseFont> fonts, BaseReportObject pParent, PropertyGetter pGetter, Report report) throws ParseException {

		_fonts = fonts;
		parent = pParent;
		propertyGetter = pGetter;
		this.report = report;
		Load(node);
	}

	public String getDateProperty(String part, String propertyName, String dateFormat) throws ParseException {
		Date s = propertyGetter.GetPropertyStringAsDate(propertyName, dateFormat);
		if (s != null) {
			Calendar ca1 = Calendar.getInstance();
			ca1.setTime(s);
			String ret = "";
			if (part.equalsIgnoreCase("day")) {
				ret = String.valueOf(ca1.get(Calendar.DAY_OF_MONTH));
			}
			if (part.equalsIgnoreCase("month")) {
				ret = String.valueOf(ca1.get(Calendar.MONTH));
				if (ret.length() == 1) ret = "0" + ret;
			}
			if (part.equalsIgnoreCase("year")) {
				ret = String.valueOf(ca1.get(Calendar.YEAR));
			}

			return ret;

		}
		return "";
	}


	public PdfPTable getPdfObject() throws DocumentException, ParseException, IOException {

		int paramMargin = 0;

		String param = propertyGetter.GetProperty(getPropertyName());

		if (getPropertyName() != null && !getPropertyName().contains("$") && parent.parent.parent.getPageNameRT() != null) {
			param = propertyGetter.GetProperty(parent.parent.parent.getPageNameRT() + getPropertyName());
		}

		if (getDateFormat() != null && getToDateFormat() != null) {
			param = ConvertPropertyToSpecificDateFormat(param);
		}

		if (getAddZero() != null && !getAddZero().equalsIgnoreCase("false") && param != null) {
			String zeros = "";
			try {
				if (Integer.parseInt(param) < 10) {
					for (int z = 0; z < getColumns() - 1; z++) {
						zeros += 0;
					}
					param = zeros + param;
				} else if (Integer.parseInt(param) < 100) {
					for (int z = 0; z < getColumns() - 2; z++) {
						zeros += 0;
					}
					param = zeros + param;
				} else if (Integer.parseInt(param) < 1000) {
					for (int z = 0; z < getColumns() - 3; z++) {
						zeros += 0;
					}
					param = zeros + param;
				}
			} catch (Exception ex) {

			}
		}

		if (param == null && getDefaultNullValue() != null) {
			param = getDefaultNullValue();
		}


		if (getPropertyExtract() != null && param != null && !param.isEmpty()) {
			StringTokenizer st = new StringTokenizer(param, ",.-", true);
			String gpe = getPropertyExtract();
			if (param.contains(".") || param.contains(",") || param.contains("-")) {
				int extIndex = Integer.parseInt(gpe);
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
					} else if (key.equals("-") && !dataFlag) {
						extData.add("");
					} else if (key.equals("-") && dataFlag) {
						dataFlag = false;
					} else {
						extData.add(key);
						dataFlag = true;
					}
				}
				if (extIndex > extData.size() - 1) {
					param = getDefaultNullValue();
				} else {
					param = extData.get(extIndex);
				}
			}
		}


		if (getSymbolAdd() != null && param != null && !param.isEmpty()) {
			int symbolCount = getColumns() - param.length();
			for (int i = 0; i < symbolCount; i++) {
				param += getSymbolAdd();
			}
		}

		String mode = getPropertyMode();
		if (mode != null) {      //Then we assume the property is date
			param = getDateProperty(mode, getPropertyName(), getDateFormat());
		}


		if (getNumeratorRt() != null && getNumeratorRt().equals("true")) {
			try {
				Integer pageNumber = report.getPageNumber();
				if (pageNumber != null && pageNumber != 0) {
					if (pageNumber < 10) {
						String zeros = "";
						for (int z = 0; z < getColumns() - 1; z++) {
							zeros += 0;
						}
						param = zeros + pageNumber;
					} else if (pageNumber < 100) {
						String zeros = "";
						for (int z = 0; z < getColumns() - 2; z++) {
							zeros += 0;
						}
						param = zeros + pageNumber;
					} else if (pageNumber < 1000) {
						String zeros = "";
						for (int z = 0; z < getColumns() - 3; z++) {
							zeros += 0;
						}
						param = zeros + pageNumber;
					} else {
						param = "ERROR";
					}
				} else {
					pageNumber = report.getPageNumber();;
					if (pageNumber != null && pageNumber != 0) {
						if (pageNumber < 10) {
							String zeros = "";
							for (int z = 0; z < getColumns() - 1; z++) {
								zeros += 0;
							}
							param = zeros + pageNumber;
						} else if (pageNumber < 100) {
							String zeros = "";
							for (int z = 0; z < getColumns() - 2; z++) {
								zeros += 0;
							}
							param = zeros + pageNumber;
						} else if (pageNumber < 1000) {
							String zeros = "";
							for (int z = 0; z < getColumns() - 3; z++) {
								zeros += 0;
							}
							param = zeros + pageNumber;
						} else {
							param = "ERROR";
						}
					}
				}
			} catch (NullPointerException npe) {
				param = "ERROR";
			}

		}


		if (param == null) param = "";
		String paramAlign = getWordAlign();
		if (param.length() < getColumns()) {
			paramMargin = getColumns() - param.length();
		}
		for (int y = 0; y < getColumns(); y++) {
			ReportCell cell = new ReportCell(getMinimumHeight(), "", getVerticalTextAlignment(), getHorizontalTextAlignment(), getFontName(), getFontWeight(), getBorderWidth(), getLeading(), getPaddings(), getUseBorderPadding(), getBGColor(), getBorderColor(), propertyGetter, _fonts, report);

			if (paramAlign.equals("right")) {
				if (y >= paramMargin) {
					String symbol = Character.toString(param.charAt(y - paramMargin));
					cell = new ReportCell(getMinimumHeight(), symbol, getVerticalTextAlignment(), getHorizontalTextAlignment(), getFontName(), getFontWeight(), getBorderWidth(), getLeading(), getPaddings(), getUseBorderPadding(), getBGColor(), getBorderColor(), propertyGetter, _fonts, report);
				}
			}
			if (paramAlign.equals("left")) {
				if (y < param.length()) {
					String symbol = Character.toString(param.charAt(y));
					cell = new ReportCell(getMinimumHeight(), symbol, getVerticalTextAlignment(), getHorizontalTextAlignment(), getFontName(), getFontWeight(), getBorderWidth(), getLeading(), getPaddings(), getUseBorderPadding(), getBGColor(), getBorderColor(), propertyGetter, _fonts, report);
				}
			}

			items.add(cell);
		}

		PdfPTable table = new PdfPTable(getColumns());
		if (getTotalWidth() > 0) table.setTotalWidth(getTotalWidth());
		if (getWidthPercentage() > 0) table.setWidthPercentage(getWidthPercentage());

		table.setHorizontalAlignment(getHorizontalAlignment());
		if (getBorderStyle() != null && getBorderStyle().equals("dotted")) {
			table.setTableEvent(new DashedTable());
			table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		}

		for (ReportCell item : items) {


			table.addCell(item.getPdfObject());
		}

		return table;
	}

}
