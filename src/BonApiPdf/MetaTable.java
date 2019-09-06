package BonApiPdf;

public class MetaTable {
	public String tableName = "";
	public String alias="";
	public String sustituyeAlias(String sql) {
		String s=sql;
		if (!("").equals(alias)){
			s = s.replace("/" + alias + "." , "/" + tableName +".");
			s = s.replace("," + alias + "." , "," + tableName +".");
			s = s.replace("*" + alias + "." , "*" + tableName +".");
			s = s.replace("+" + alias + "." , "+" + tableName +".");
			s = s.replace("-" + alias + "." , "-" + tableName +".");
			s = s.replace("(" + alias + "." , "(" + tableName +".");
			s = s.replace(" " + alias + "." , " " + tableName +".");
	
			// ahora quitamos el alias de la parte del FROM
			s = s.replace(" " + tableName + " " + alias + " " , " " + tableName +" ");
			s = s.replace("," + tableName + " " + alias + " " , "," + tableName +" ");
			s = s.replace(" " + tableName + " " + alias + "," , " " + tableName +",");
			s = s.replace("," + tableName + " " + alias + "," , "," + tableName +",");
		}
		return s;
	}
}
