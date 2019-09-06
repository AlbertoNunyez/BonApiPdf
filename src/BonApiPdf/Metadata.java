package BonApiPdf;

//import java.util.List;
public class Metadata {
	public enum tipos{COMPLEX, TEXT, INTEGER, LONG, BOOLEAN, DOUBLE, DATE};
	public String id="";
	public String label="";
	public tipos type=tipos.COMPLEX;
	public boolean editable=true;
	public boolean esPrimaryKey=false;
	public boolean esForeignKey=false;
	public int columnId=0; 
	//public List<java.lang.Object> metadata;
	public metaDatas metadataDep;
	public String pkColumnName="";
	public String pkTableName=""; 
	public String catalogName="";
	public String tableName="";
	public String originalColumnText="";
	public String condicionWhere="";
	public String currentField_id="";
	public String getJSon() {
		String s="{";
    	s+="\"id\":\"" + id + "\"";
    	switch(type) {
    	case COMPLEX:
        	s+=",\"type\":\"complex\"";
    		break;
    	case TEXT:
        	s+=",\"type\":\"text\"";
    		break;
    	case BOOLEAN:
        	s+=",\"type\":\"boolean\"";
    		break;
    	case INTEGER:
    	case LONG:
        	s+=",\"type\":\"integer\"";
    		break;
    	case DOUBLE:
        	s+=",\"type\":\"double\"";
    		break;
    	case DATE:
        	s+=",\"type\":\"date\"";
    		break;
    	default:
        	s+=",\"type\":\"text\"";
    	}
    	if (esPrimaryKey)
    		editable = false;
    	s+=",\"editable\":" + editable + "";
    	if (metadataDep != null && metadataDep.metadataList.size()>0) {
    		s+=",\"metadata\":" + metadataDep.getJSon() + "";
    	}
		s+="}";
		return s;
	}
	public String getCompleteSQL(ReferenceInt nodoPath) {
		String s="";
		currentField_id=nodoPath.getNextField() + "__" + id;
		if(("").equals(tableName)) {
			s+= this.originalColumnText + " as " + currentField_id +  ",";
		}else {
			s+= tableName + "." + id + " as " + currentField_id +  ",";
		}
    	if (metadataDep != null && metadataDep.metadataList.size()>0) {
    		s+= metadataDep.getCompleteSQL(nodoPath) + ",";
    	}
		return s;
	}
	public String getCompleteSQLFROM() {
		String s="";
		if (!("").equals(tableName)) {
			s+= tableName + ",";
		}
    	if (metadataDep != null && metadataDep.metadataList.size()>0) {
    		s+= metadataDep.getCompleteSQLFROM() + "";
    	}
		return s;
	}
	public String getCompleteSQLWHERE() {
		String s="";
		Metadata unMeta= new Metadata();
		if (tableName.equals("embpaises")) {
			System.out.println("embpaises." + id + " tipo = " + type);
		}
    	switch(type) {
    	case COMPLEX:
    		unMeta = metadataDep.getPrimaryKeyMeta(); 
        	s+=tableName +"." + id + " = " + unMeta.tableName + "." + unMeta.id + " and "; 
    		break;
    	default:
    		if (metadataDep != null) {
        		unMeta = metadataDep.getPrimaryKeyMeta(); 
            	s+=tableName +"." + id + " = " + unMeta.tableName + "." + unMeta.id + " and "; 
    		}
    		break;
    	}
    	if (metadataDep != null && metadataDep.metadataList.size()>0) {
    		s+= metadataDep.getCompleteSQLWHERE() + "";
    	}
		return s;
	}
	public String getQuoteString(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        char         c = 0;
        int          i;
        int          len = string.length();
        String       t;
        StringBuilder sb = new StringBuilder(len + 4);
		try {

	        sb.append('"');
	        for (i = 0; i < len; i += 1) {
	            c = string.charAt(i);
	            switch (c) {
	            case '\\':
	            case '"':
	                sb.append('\\');
	                sb.append(c);
	                break;
	            case '/':
//	                if (b == '<') {
	                    sb.append('\\');
//	                }
	                sb.append(c);
	                break;
	            case '\b':
	                sb.append("\\b");
	                break;
	            case '\t':
	                sb.append("\\t");
	                break;
	            case '\n':
	                sb.append("\\n");
	                break;
	            case '\f':
	                sb.append("\\f");
	                break;
	            case '\r':
	               sb.append("\\r");
	               break;
	            default:
	                if (c < ' ') {
	                    t = "000" + Integer.toHexString(c);
	                    sb.append("\\u" + t.substring(t.length() - 4));
	                } else {
	                    sb.append(c);
	                }
	            }
	        }
	        sb.append('"');
		}catch(Exception ex) {
			
		}
        return sb.toString();
    }
}
