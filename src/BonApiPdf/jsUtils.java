package BonApiPdf;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class jsUtils {
	private static String joinArray(String[] parts, int Desde, String separador) {
		String s="";
		int i=0;
		for(i=Desde; i< parts.length;i++) {
			s += parts[i];
			if(i<(parts.length-1)) {
				s += separador; 
			}
		}
		return s;
	}
	static JSONObject findJSObject(String nomItem, JSONArray jsMetaData) {
		JSONObject s=null;
		JSONObject unCandidato=null;
		int i=0;
		String dato="";
		for (i=0; i< jsMetaData.size();i++) {
			unCandidato = (JSONObject) jsMetaData.get(i);
			dato = getJSdato("id", unCandidato);
			if (dato.equals(nomItem)){
				s = unCandidato;
				break;
			}
		}
		return s;
	}
	static String getJSMetadato(String nomItem, JSONArray jsMetaData, String porDefecto) {
		String s=porDefecto;
		JSONObject unMeta=null;
		try {
			if (jsMetaData != null) {
				String[] parts = nomItem.split("\\.");
				if (parts.length<=1) {
					unMeta = findJSObject(parts[0],jsMetaData);
					s = getJSdato("type",unMeta,porDefecto);
				}else {
					unMeta = findJSObject(parts[0],jsMetaData);
					if (unMeta != null) {
						JSONArray jsMetaSon = (JSONArray) unMeta.get("metadata");
						String nomItemSon = joinArray(parts,1,"\\.");
						s = getJSMetadato(nomItemSon,jsMetaSon, porDefecto);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			s=porDefecto;
			e.printStackTrace();
		}
		return s;
	}
	static String getJSMetadato(String nomItem, JSONArray jsMetaData) {
		return getJSMetadato(nomItem, jsMetaData, "");
	}
	
	static String getJSdato(String nomItem, JSONObject jsObj, String porDefecto) {
		String s=porDefecto;
		try {
			if (jsObj != null) {
				String[] parts = nomItem.split("\\.");
				if (parts.length<=1) {
					if (jsObj.containsKey(nomItem)) {
						s = jsObj.get(nomItem).toString();
					}
				}else {
					JSONObject jsObjSon = (JSONObject) jsObj.get(parts[0]);
					String nomItemSon = joinArray(parts,1,".");
					s = getJSdato(nomItemSon,jsObjSon, porDefecto);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	static String getJSdato(String nomItem, JSONObject jsObj) {
		return getJSdato(nomItem, jsObj, "");
	}
	static Boolean getJSdatoBool(String nomItem, JSONObject jsObj, Boolean porDefecto) {
		Boolean s = porDefecto;
		try {
			String[] parts = nomItem.split("\\.");
			if (parts.length<=1) {
				s = jsObj.get(nomItem).toString().toLowerCase().equals("true");
			}else {
				JSONObject jsObjSon = (JSONObject) jsObj.get(parts[0]);
				String nomItemSon = joinArray(parts,1,".");
				s = getJSdatoBool(nomItemSon,jsObjSon, porDefecto);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	static Boolean getJSdatoBool(String nomItem, JSONObject jsObj) {
		return getJSdatoBool(nomItem, jsObj, false);
	}
	static Boolean esWindows() {
		Boolean resultado = true;
		String SO = System.getProperty("os.name");
		if (SO.contains("indow")) {
			resultado=true;
		}else {
			resultado=false;
		}
		return resultado;
	}
	static String currentPath() {
		String currentDirectory="";
		if (jsUtils.esWindows()) {
			currentDirectory = System.getProperty("user.dir");
		}else {
			currentDirectory = System.getProperty("catalina.base");
		}
		return currentDirectory;
	}
}
