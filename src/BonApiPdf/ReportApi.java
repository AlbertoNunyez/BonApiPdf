package BonApiPdf;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class ReportApi {
	public static Logger logger=null;
	public String version = PDFService.version;
	private static float marginYPie = 5f; 
	private static float marginXPie = 5f;
	private static int maxLinesPerTable = 25;
	private static float maxheithFoto = 60f; 
	
	
	public ReportApi(Logger unLogger, String unaVersion) {
		logger = unLogger;
		version = unaVersion;
	}
	private static void ponerFoto(float x, float y, String ulrFoto, Document document) {
		String imageUrl = ulrFoto;
		try {
			Image image2 = Image.getInstance(new URL(imageUrl));
			float h1 = image2.getHeight();
			float w1 = image2.getWidth();
			float ratio = w1/h1;
			//image2.scaleAbsoluteHeight(10f);
			image2.scaleAbsolute(maxheithFoto*ratio, maxheithFoto);
			y = y-maxheithFoto/2f;
			image2.setAbsolutePosition(x, y);
			document.add(image2);
		} catch (BadElementException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * @param jsObj
	 * @param document
	 */
	private static void printCabecera(JSONObject jsObj, Document document) {
		try {
			
			document.addHeader("Titulo", jsUtils.getJSdato("titulo",jsObj)); // solo sirve para añadir propiedades personalizadas que se ven en las propiedades del PDF
			Paragraph myPara1= new Paragraph();
			myPara1.setAlignment(Element.ALIGN_CENTER);
			myPara1.setFont(FontFactory.getFont(FontFactory.TIMES_BOLD, 15));
			myPara1.add(jsUtils.getJSdato("titulo",jsObj));
			myPara1.setFont(FontFactory.getFont(FontFactory.TIMES_BOLD, 12));
			myPara1.add("\n" + jsUtils.getJSdato("asunto",jsObj) + "\n\n");
			document.add(myPara1);
//			Paragraph myPara2= new Paragraph(jsUtils.getJSdato("asunto",jsObj) + "\n\n\n");
//			myPara2.setAlignment(Element.ALIGN_CENTER);
//			document.add(myPara2);

			String imageUrl = jsUtils.getJSdato("logo",jsObj);
			ponerFoto(1f, document.top(), imageUrl, document);
			//image2.setAbsolutePosition(1f, document.top()-image2.getHeight()/2);
			//document.add(image2);
		} catch (BadElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		       
	}
	  private static void absText(String text, float x, float y, int Alineacion, PdfWriter writer) {
		    try {
		      PdfContentByte cb = writer.getDirectContent();
		      BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		      cb.saveState();
		      cb.beginText();
		      cb.moveText(x, y);
		      cb.setFontAndSize(bf, 12);
	//	      cb.showText(text);
		      cb.showTextAligned(Alineacion,text, x, y, 0);
		      cb.endText();
		      cb.restoreState();
		    } catch (DocumentException e) {
		      e.printStackTrace();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		  }	
	private static void printPie(JSONObject jsObj, Document document, PdfWriter writer) {
		String s = jsUtils.getJSdato("pie",jsObj);
		Boolean showPagina = jsUtils.getJSdatoBool("showPagina",jsObj);
		Boolean showFecha = jsUtils.getJSdatoBool("showFecha",jsObj); //jsObj.get("showFecha").toString().equals("true");
		absText(s,marginXPie,marginYPie,Element.ALIGN_LEFT,writer);
		if (showFecha) {
			Calendar unaFecha =Calendar.getInstance(); 
			s = String.format("%td/%tm/%tY", unaFecha, unaFecha, unaFecha);
			absText(s,document.right()/2f,marginYPie,Element.ALIGN_CENTER,writer);
		}
		if(showPagina){
			s = " Pág. " + writer.getPageNumber();
			absText(s,document.right(),marginYPie,Element.ALIGN_RIGHT,writer);
		}
	}
	private static PdfPTable printCabeceraSeccion(JSONObject jsSeccion, Document document) {
		JSONArray jsCampos = (JSONArray) jsSeccion.get("campos");
		JSONArray jsTitulos = (JSONArray) jsSeccion.get("titulos");
		JSONArray colWidths = (JSONArray) jsSeccion.get("colWidths");
		PdfPTable table = null; 
		try {
			int iCampo=0;
			Paragraph myTituloSeccion= new Paragraph();
			myTituloSeccion.setIndentationLeft(45f);
			myTituloSeccion.setFont(FontFactory.getFont(FontFactory.TIMES_BOLD, 12));
			myTituloSeccion.setAlignment(Element.ALIGN_LEFT);
			myTituloSeccion.add(jsUtils.getJSdato("tituloSeccion",jsSeccion));
			document.add(myTituloSeccion);
			table = new PdfPTable(jsCampos.size()); 
			table.setWidthPercentage(85); //Width 100%
			table.setSpacingBefore(10f); //Space before table
			table.setSpacingAfter(0f); //Space after table
			//Set Column widths
			float[] columnWidths = new float[jsCampos.size()];//colWidths.toArray();//{2f, 2f, 1f};
			for(iCampo=0;iCampo<jsCampos.size();iCampo++) {
				columnWidths[iCampo] = Float.parseFloat(colWidths.get(iCampo).toString());
			}
			table.setWidths(columnWidths);
			FontSelector selector = new FontSelector();
			Font f1 = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12);
			f1.setColor(BaseColor.WHITE);
			selector.addFont(f1);
			Phrase ph = selector.process("H1");
			PdfPCell header1 = new PdfPCell(ph);
			header1.setBorder(1);
			header1.setBorderColor(BaseColor.BLACK);
			header1.setBackgroundColor(new BaseColor(17,81,126));//BaseColor.CYAN);
			//header1.setBorderColorBottom(BaseColor.BLACK);
			header1.setBorderColorLeft(BaseColor.WHITE);
			header1.setBorderColorRight(BaseColor.WHITE);
			header1.setBorderColorTop(BaseColor.WHITE);
			
			header1.setPaddingLeft(10);
			header1.setHorizontalAlignment(Element.ALIGN_CENTER);
			header1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			if (jsTitulos.size()==0) {
				jsTitulos=jsCampos;
			}
			for(iCampo=0;iCampo<jsTitulos.size();iCampo++) {
				Phrase phCampo = selector.process(jsTitulos.get(iCampo).toString());
				header1.setPhrase(phCampo);
				table.addCell(header1);
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return table;
	}
	private static void printSeccion(String nomSeccion, JSONObject jsObj, Document document, PdfWriter writer) {
		try {
			JSONObject jsDatos = (JSONObject) jsObj.get("datos");
			JSONObject jsSeccion = (JSONObject) jsDatos.get(nomSeccion);
			JSONArray jsCampos = (JSONArray) jsSeccion.get("campos");
			JSONObject jsDatosSeccion = (JSONObject) jsSeccion.get("datos");
			JSONArray jsData = (JSONArray) jsDatosSeccion.get("data");
			JSONArray jsMetaData = (JSONArray) jsDatosSeccion.get("metadata");

			printCabecera(jsObj,document);
			PdfPTable table = printCabeceraSeccion(jsSeccion,document);//new PdfPTable(jsCampos.size()); 
			int iCampo=0;
			int i=0;
			int lineas=0;
			for(i=0;i<jsData.size();i++) {
				JSONObject jsRecord = (JSONObject) jsData.get(i);
				for(iCampo=0;iCampo<jsCampos.size();iCampo++) {
					String nomCampo = jsCampos.get(iCampo).toString();
					String dato = jsUtils.getJSdato(nomCampo, jsRecord);//jsRecord.get(nomCampo).toString();
					String tipo = jsUtils.getJSMetadato(nomCampo, jsMetaData);
					//PdfPCell cell1 = new PdfPCell(new Paragraph(dato));
					PdfPCell cell1 = new PdfPCell();
					cell1.setBorderColor(BaseColor.BLUE);
					//cell1.setBorder(Rectangle.BOTTOM | Rectangle.RIGHT);
					cell1.setBorder(Rectangle.BOTTOM);
					cell1.setBorderWidthBottom((float) 0.5);
					cell1.setBorderColorBottom(BaseColor.BLACK);
					cell1.setPaddingLeft(10);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					Paragraph paraph1 = new Paragraph();
					paraph1.setFont(FontFactory.getFont(FontFactory.TIMES_ROMAN, 10));
					switch(tipo) {
					case "integer":
						paraph1.add(dato);
						paraph1.setAlignment(Element.ALIGN_RIGHT);
						break;
					case "double":
						paraph1.add(String.format("%.2f", Float.parseFloat(dato)));
						paraph1.setAlignment(Element.ALIGN_RIGHT);
						break;
					default:
						paraph1.add(dato);
						paraph1.setAlignment(Element.ALIGN_LEFT);
					}
					cell1.addElement(paraph1);
					table.addCell(cell1);
				}
				if (lineas++> maxLinesPerTable) {
					document.add(table);
					printPie(jsObj,document,writer);
					document.newPage();
					lineas=0;
					printCabecera(jsObj,document);
					table = printCabeceraSeccion(jsSeccion,document);//new PdfPTable(jsCampos.size()); 
				}
			}
			document.add(table);
			printPie(jsObj,document,writer);
			document.newPage();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		       
	}

	public static void getReport(JSONObject jsObj, Document document, PdfWriter writer) {
		try{
			document.open();
			//Set attributes here
			
			document.addAuthor("Neith BP Consulting, S.L.");
			document.addCreationDate();
			document.addCreator("neithBP.com");
			document.addTitle(jsUtils.getJSdato("titulo",jsObj));
			document.addSubject(jsUtils.getJSdato("asunto",jsObj));//jsObj.get("asunto").toString());
			//Add more content here
			JSONArray jsSecciones = (JSONArray) jsObj.get("secciones");
			if (jsSecciones.size() >0 ) {
				int i;
				for(i=0;i<jsSecciones.size();i++) {
					String nomSeccion  =  jsSecciones.get(i).toString();
					printSeccion(nomSeccion, jsObj,document,writer);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		document.close();
	}
}
