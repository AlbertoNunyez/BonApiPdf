package BonApiPdf;
import org.json.simple.JSONObject;

public class responseRest {
	public String code="";
	public String message="";
	@SuppressWarnings("unchecked")
	public String getJSon() {
		JSONObject jsO=new JSONObject ();
		jsO.put("code", code);
		jsO.put("message", message);
		String s = "";
    	JSONObject jsO2 = new JSONObject();
    	jsO2.put("status", jsO);
    	s = jsO2.toJSONString();
    	return s;
	}
}
