package BonApiPdf;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Logger;

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
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
// https://howtodoinjava.com/library/read-generate-pdf-java-itext/#itext_hello_world
public class PdfApi {
	public static Logger logger=null;
	public String version = PDFService.version;
	
	public static void createHellowWorld()
	{
		Document document = new Document();
		try
		{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("HelloWorld2.pdf"));
			document.open();
			document.add(new Paragraph("A Hello World PDF document Alberto."));
			document.close();
			writer.close();
		} catch (DocumentException e){
			e.printStackTrace();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
	}
	private static void printDemoCabecera(Document document) {
		try {
			document.addHeader("Titulo", "Esto es el título del Header"); // solo sirve para añadir propiedades personalizadas que se ven en las propiedades del PDF
			String imageUrl = "http://www.neithbp.com/public/imgs/logo.png";
			Image image1 = Image.getInstance(new URL(imageUrl));
			image1.setAbsolutePosition(10f, 55f);
			document.add(image1);
		} catch (BadElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		       
	}
	private static void printDemoPage(Document document) {
		try {
			PdfPTable table = new PdfPTable(3); // 3 columns.
			table.setWidthPercentage(80); //Width 100%
			table.setSpacingBefore(10f); //Space before table
			table.setSpacingAfter(0f); //Space after table
			//Set Column widths
			float[] columnWidths = {2f, 2f, 1f};
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
			table.addCell(header1);
			ph = selector.process("H2");
			header1.setPhrase(ph);
			table.addCell(header1);
			ph = selector.process("H3");
			header1.setPhrase(ph);
			table.addCell(header1);

			
			PdfPCell cell1 = new PdfPCell(new Paragraph("Cell 1 muy grande"));
			//cell1.setBorderColor(BaseColor.BLUE);
			cell1.setBorder(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setPaddingLeft(10);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			
			PdfPCell cell2 = new PdfPCell(new Paragraph("Cell 2"));
			//cell2.setBorderColor(BaseColor.GREEN);
			cell2.setBorder(1);
			cell2.setBorderColorTop(BaseColor.BLACK);
			cell2.setPaddingLeft(1);
			cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			
			PdfPCell cell3 = new PdfPCell(new Paragraph(""));
			
			//cell3.setBorderColor(new BaseColor(17,81,126));
			cell3.setBorderColor(new BaseColor(78,128,2));
			cell3.setPaddingLeft(10);
			cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			
			//To avoid having the cell border and the content overlap, if you are having thick cell borders
			//cell1.setUserBorderPadding(true);
			//cell2.setUserBorderPadding(true);
			//cell3.setUserBorderPadding(true);
			
			table.addCell(cell1);
			table.addCell(cell2);
			table.addCell(cell3);
			cell1.setPhrase(new Paragraph("Cell4"));
			cell1.setBorder(0);
			cell1.setBorderColorTop(BaseColor.BLACK);
			table.addCell(cell1);
			cell2.setBorder(0);
			cell2.setBorderColorTop(BaseColor.BLACK);
			cell2.setPhrase(new Paragraph("Cell5"));
			table.addCell(cell2);
			table.addCell(cell3);
			int i=0;
			for (i=0; i< 36; i++) {
				table.addCell(cell1);
				table.addCell(cell2);
				table.addCell(cell3);
			}
			header1.setPhrase(new Paragraph("H1"));
			table.addCell(header1);
			header1.setPhrase(new Paragraph("H2"));
			table.addCell(header1);
			header1.setPhrase(new Paragraph("H3"));
			table.addCell(header1);
			
			document.add(table);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		       
	}
	
	private static void printDemoPie(Document document, PdfWriter writer) {
		try {
			Paragraph myPara= new Paragraph("neithBP.com at " + new Date().toString() + " Pág. " + writer.getPageNumber());
			myPara.setAlignment(Element.ALIGN_RIGHT);
			document.add(myPara);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void printDemoPDF(Document document, PdfWriter writer) {
		try{
			document.open();
			//Set attributes here
			
			document.addAuthor("Neith BP Consulting, S.L.");
			document.addCreationDate();
			document.addCreator("neithBP.com");
			document.addTitle("Documento de prueba");
			document.addSubject("Prueba de utilización de escritura de archivos PDF.");
			//Add more content here

			printDemoCabecera(document);
			printDemoPage(document);
			//document.setPageCount(document.getPageNumber()+1);
			printDemoPie(document,writer);
			document.newPage();
			printDemoCabecera(document);
			printDemoPage(document);
			//document.setPageCount(document.getPageNumber()+1);
			printDemoPie(document,writer);
			document.newPage();
			printDemoCabecera(document);
			printDemoPage(document);
			//document.setPageCount(document.getPageNumber()+1);
			printDemoPie(document,writer);
			document.newPage();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		document.close();
	}

	public static void getDemoPDF(OutputStream unStream) {
		Document document = new Document();
		try {
			PdfWriter writer = PdfWriter.getInstance(document, unStream);
			printDemoPDF(document,writer);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void getDemoPDF(OutputStream unStream, String unJson ) {
		Document document = new Document();
		try {
			PdfWriter writer = PdfWriter.getInstance(document, unStream);
			printDemoPDF(document,writer);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void getDemoPDF(String fichero) {
		Document document = new Document();
		try {
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fichero));
			printDemoPDF(document,writer);
			writer.close();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
	}
	public static void getReportPDF(OutputStream unStream, JSONObject unJson ) {
		Document document = new Document();
		try {
			PdfWriter writer = PdfWriter.getInstance(document, unStream);
			//ReportApi unReport = new ReportApi(logger,"" );
			ReportApi.getReport(unJson,document,writer);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String getReportPDF(JSONObject jsObj, String nomFichero) {
		Document document = new Document();
		String resultado = "NOK";
		try {
			logger.info("Estamos dentro: " + nomFichero);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(nomFichero));
			ReportApi unReport = new ReportApi(logger,"" );
			unReport.getReport(jsObj,document,writer);
			writer.close();
			return "OK";
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			resultado += ".error = " + e.getMessage();
			e.printStackTrace();
		} catch (FileNotFoundException e){
			e.printStackTrace();
			resultado += ".error = " + e.getMessage();
		}
		return resultado;
	}
}
