package BonApiPdf;

import java.io.IOException;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.util.regex.Pattern;

//import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
//import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
//import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
//https://docs.oracle.com/cd/E19798-01/821-1841/6nmq2cp1v/index.html
//http://notasjs.blogspot.com.es/2013/07/http-diferencia-entre-post-y-put.html
// PUT sirve para crear/modificar un recurso en un sitio concreto especificado en la URL
// POST es más genérico, y puede usarse para lo que sea, pero en nuestro caso se podrá usar para crear un nuevo recurso dentro de una lista especificada pero sin asignar una ID preestablecida.
//1- Help=>Install New Software => Work with => All Available sites (revisar componentes y listar)
//2- Eclipse => exportar => Web => War
//3- Copiar War en directorio de Tomcat "C:\BonitaBPM-7.5.1\workspace\tomcat\server\webapps"
//4- Para hacer pruebas REST desde FireFox => Añadir Plugin Http Requester
//http://localhost:8080/SIM_NeithBP/rest/SIMService/version

// Para obtener los jars que puedan faltar:
//https://mvnrepository.com/artifact/com.sun.jersey

// Ejemplo de como subir una imagen a un servicio REST
//https://javatutorial.net/java-file-upload-rest-service

//Ejemplo de como configurar servicios REST
//https://www.programcreek.com/java-api-examples/?class=javax.ws.rs.core.MediaType&method=MULTIPART_FORM_DATA

// Para testear:
// http://localhost:8080/SIM_NeithBP/rest/SIMService/version


@Path("/PDFService") 
public class PDFService {
	static String version = "0.1.1";
	private boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
		    getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
	private static Logger logger = Logger.getLogger("SimService.log");	
	
    @GET 
    @Path("/version") 
    @Produces(MediaType.APPLICATION_JSON) 
    public String getVersion(){
		logger.setLevel(isDebug ? Level.ALL : Level.WARNING);
		//    	GenericAuthenticationService.checkUserCredentials(null);
		//PdfApi.createHellowWorld();
		String miPath = "";
		miPath = jsUtils.currentPath();
		miPath = miPath.replace("\\", "\\\\");
		return "{\"version\": \"" + version + "\", \"mode\": \"" + (isDebug ? "DEBUG MODE" : "RUN") +"\", \"path\": \"" + miPath + "\"}"; 
    }  

    @GET 
    @Path("/pdfDemo") 
    @Produces("application/pdf") 
    public Response pdfDemo() {
    	logger.setLevel(isDebug ? Level.ALL : Level.WARNING);
        javax.ws.rs.core.Response.ResponseBuilder responseBuilder = Response.ok();
        Response myResponse = null;
        String unJson="{dato:8}";
		try {
			StreamingOutput unStream = new StreamingOutput() {
				@Override
				public void write(final OutputStream output) throws IOException, WebApplicationException {
					PdfApi.getDemoPDF(output,unJson);
				}
			};
			responseBuilder = javax.ws.rs.core.Response.ok((Object) unStream);
	        responseBuilder.type("application/pdf");
	        responseBuilder.header("Content-Disposition", "filename=test.pdf");
	        myResponse=responseBuilder.build();
		} catch (Exception ex ) {
			return Response.status(200)
					.entity("ERROR creating PDF: " + ex.getMessage()).build();
		}
        return myResponse; 
    }

    @POST
    @Path("/getPDF") 
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/pdf") 
    public Response getPDF(String incomingData) {
    	logger.setLevel(isDebug ? Level.ALL : Level.WARNING);
        javax.ws.rs.core.Response.ResponseBuilder responseBuilder = Response.ok();
        Response myResponse = null;
        //String unJson= incomingData;
		Object obj=null;
		try {
			obj = new JSONParser().parse(incomingData);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject unJson = (JSONObject) obj;
		try {
			StreamingOutput unStream = new StreamingOutput() {
				@Override
				public void write(final OutputStream output) throws IOException, WebApplicationException {
					PdfApi.getReportPDF(output,unJson);
				}
			};
			responseBuilder = javax.ws.rs.core.Response.ok((Object) unStream);
	        responseBuilder.type("application/pdf");
	        responseBuilder.header("Content-Disposition", "filename=test.pdf");
	        myResponse=responseBuilder.build();
		} catch (Exception ex ) {
			return Response.status(200)
					.entity("ERROR creating PDF: " + ex.getMessage()).build();
		}
        return myResponse; 
    }
    
    @POST
    @Path("/savePDF") 
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public String savePDF(String incomingData) {
		String resultado="hola4";
		String linuxPath="";
		String name = "";
    	logger.setLevel(isDebug ? Level.ALL : Level.WARNING);
		Object obj=null;
		try {
			obj = new JSONParser().parse(incomingData);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			resultado = e.getMessage();
			return "{\"resultado\": \"" + resultado +"\"}"; 
		}
		try {
			PdfApi.logger = logger;
			JSONObject unJson = (JSONObject) obj;
			String separador = "/";
			if (jsUtils.esWindows()) {
				name = jsUtils.currentPath() + "\\webapps\\Archivos\\";
				separador = "\\";
			}else {
				linuxPath = "/webapps/Archivos/";
				name = jsUtils.currentPath();
			}
			linuxPath += jsUtils.getJSdato("casoID", unJson,"sinCaso") + separador;
			File files = new File(name + linuxPath);
			if (!files.exists()) {
				files.mkdirs();
			}
			linuxPath += jsUtils.getJSdato("nomFichero", unJson,"report3.pdf");
			name += linuxPath;
			resultado = PdfApi.getReportPDF(unJson, name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			resultado = e.getMessage();
		}
		if (resultado.equals("OK")) {
			if (jsUtils.esWindows()) {
				name = name.replace("\\", "\\\\");
			}else {
				name = linuxPath;
				name = name.replace("/webapps", "");
			}
			resultado = name;
		}
		return "{\"resultado\": \"" + resultado + "\"}"; 
    }

	public static String readFileAsString(String fileName)throws Exception 
	  { 
	    String data = ""; 
	    data = new String(Files.readAllBytes(Paths.get(fileName)), "UTF-8");//"ISO-8859-1"); 
	    return data; 
	  } 
    public static void main(String[] args) throws Exception {
        // check that Bonita Home is set
    	PDFService unMe = new PDFService();
    	
    	logger.info("· Starting UserService.main(String[] args)" + unMe.getVersion());
    	String incomingData=readFileAsString(".\\sampleData\\report.json");
		Object obj = new JSONParser().parse(incomingData);
		JSONObject jsObj = (JSONObject) obj;
		PdfApi.logger = logger;
    	PdfApi.getReportPDF(jsObj, ".\\sampleData\\report.pdf");
        logger.info("Completed sucessfully!!!");
    }
}
