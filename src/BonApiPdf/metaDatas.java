package BonApiPdf;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author alber
 *
 */
public class metaDatas {
	public List<Metadata> metadataList=null;
	private Map<String, ResultSet> rsDependencias=null;
	private String ListaCamposOriginal[];
	private String fromClauseOriginal="";
	private List<MetaTable> metaTableList=null; 
    private String getColumnsPartFromSQL(String sql) {
    	String s="";
    	String lines[] = sql.split("(?i) from "); // se trata de obtner la misma lista de columnas que la sql original.
    	// para evitar tomar un where de una subquery hay que recorrer el array y contar los paréntesis abiertos y cerrados, cuando el número de abiertos sea igual al de cerrados entonces podemos quedarnos con el resto de strings porque esos son los de la SQL principal
    	int i=0;
    	int numAbiertos=0;
    	int numCerrados=0;
    	for(i=0; i< lines.length; i++) {
    		numAbiertos+=lines[i].chars().filter(ch -> ch == '(').count();
    		numCerrados+=lines[i].chars().filter(ch -> ch == ')').count();
    		if (numAbiertos==numCerrados) {
    			break;
    		}
    	}
    	int j=0;
    	for(j=0;j<= i; j++) {
    		s+= lines[i];
    	}
    	// ahora en s tenemos la parte de las columnas, solo falta quitar el primer select
    	s=s.replaceFirst("(?i)select ", "");
    	return s;
    }
    private String getUntilASpart(String sql) {
    	String s="";
		String lines[] = sql.split("(?i) as "); // ahora separamos por el operador "as"
    	if (lines.length>0) {
    		s = lines[0];
    	}
    	return s;
    }
    /**
     * Descripción getOriginalFrom => obtiene la lista de tablas de la sql original
     * @param sql
     */
    public void getOriginalFrom(String sql) {
		String lines[] = sql.split("(?i) FROM "); // ahora separamos por la commna, pero hay que tener en cuenta que pueden haber subquerys
		int i=0;
    	int numAbiertos=0;
    	int numCerrados=0;
    	fromClauseOriginal="";
    	if (lines.length>1) {
			for(i=0; i< lines.length; i++) {
				numAbiertos+=lines[i].chars().filter(ch -> ch == '(').count();
				numCerrados+=lines[i].chars().filter(ch -> ch == ')').count();
				if (numAbiertos==numCerrados) {
					break;
				}
			}
			String fromlist[] = lines[i+1].split("(?i) WHERE "); // quitamos la parte del where y nos quedamos con el primer elemento de la lista
			if (fromlist.length<=1) {
				fromlist = lines[i+1].split("(?i) ORDER "); // quitamos la parte del where y nos quedamos con el primer elemento de la lista
			}
			if (fromlist.length<=1) {
				fromlist = lines[i+1].split("(?i) GROUP "); // quitamos la parte del where y nos quedamos con el primer elemento de la lista
			}
			fromClauseOriginal=fromlist[0];
			// ahora guardamos las tablas y sus alias en la metaTableList
			metaTableList = new ArrayList<MetaTable>();
			MetaTable unMetaTab=null;
			lines = fromClauseOriginal.split(",");
			
			for(i=0; i< lines.length; i++) {
				unMetaTab=new MetaTable();
				String subLines[]=lines[i].trim().split(" ");
				unMetaTab.tableName=subLines[0];
				if(subLines.length>1) {
					unMetaTab.alias=subLines[1];
				}else {
					unMetaTab.alias="";
				}
				metaTableList.add(unMetaTab);
			}
    	}
    }
	/**
	 * Descripción getOriginalColumns => Extrae todas las columnas de la SQL original y las guarda en el array ListaCamposOriginal
	 * @param sql => Es la sql original que nos ha pasado el cliente y que debemos desmenuzar
	 * @param n => Indica el número de campos que deberían aparecer, este dato debe venir número de items de la lista de metadatas
	 */
	private void getOriginalColumns(String sql, int n) {
		String columnsPart = getColumnsPartFromSQL(sql);
		if (("*").equals(columnsPart) || ("").equals(columnsPart)||(columnsPart.indexOf("*")>=0)){
			ListaCamposOriginal = null;
			return;
		}
		String lines[] = columnsPart.split(","); // ahora separamos por la commna, pero hay que tener en cuenta que pueden haber subquerys
    	int i=0;
    	int numAbiertos=0;
    	int numCerrados=0;
    	String columna = "";
    	int numCamposAddeed=0;
    	ListaCamposOriginal = new String[n];
    	try {
			for(i=0; i< lines.length; i++) {
				numAbiertos+=lines[i].chars().filter(ch -> ch == '(').count();
				numCerrados+=lines[i].chars().filter(ch -> ch == ')').count();
				columna+=lines[i] + ",";
				if (numAbiertos==numCerrados) {
					// podemos añadir la columna a  la lista
					columna =columna.substring(0,columna.length()-1); // quitamos la última coma 
			    	ListaCamposOriginal[numCamposAddeed] = getUntilASpart(columna);
			    	numAbiertos=0;
			    	numCerrados=0;
			    	columna = "";
			    	numCamposAddeed++;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	}
	/**
	 * setMetadata: dado el resultado de una consulta, construye la lista de metadata de cada una de las columnas de la consulta
	 * @param c : connexion abierta
	 * @param rsmd : resultset correspondiente a la consulta de la que se quiere obtener el metadata
	 * @param dbmd : Metadata de la base de datos consultada
	 * @return : 
	 */
	
	public void setMetadata(Connection c,String sql, ResultSetMetaData rsmd, DatabaseMetaData dbmd) {
		Integer i=0;
		Metadata unMeta=null;

		try {
			getOriginalColumns(sql,rsmd.getColumnCount());
			getOriginalFrom(sql);
			ResultSet pKs = dbmd.getPrimaryKeys(rsmd.getCatalogName(1), null, rsmd.getTableName(1));
			metadataList=new ArrayList<Metadata>();
			for (i=1; i <= rsmd.getColumnCount(); i++) {
				unMeta = new Metadata();
				unMeta.columnId=i;
				unMeta.id = rsmd.getColumnName(unMeta.columnId);
				unMeta.label = rsmd.getColumnLabel(unMeta.columnId);
        		switch(rsmd.getColumnType(unMeta.columnId)) {
        		case Types.VARCHAR:
        			unMeta.type=Metadata.tipos.TEXT;
        			break;
        		case Types.INTEGER:
        		case Types.DECIMAL:
        		case Types.NUMERIC:
        		case Types.SMALLINT:
        		case Types.TINYINT:
        			unMeta.type=Metadata.tipos.INTEGER;
        			break;
        		case Types.BIGINT:
        			unMeta.type=Metadata.tipos.LONG;
        			break;
        		case Types.BIT:
        		case Types.BOOLEAN:
        			unMeta.type=Metadata.tipos.BOOLEAN;
        			break;
        		case Types.DOUBLE:
        		case Types.REAL:
        			unMeta.type=Metadata.tipos.DOUBLE;
        			break;
        		case Types.DATE:
        			unMeta.type=Metadata.tipos.DATE;
        			break;
        		default:
        			unMeta.type=Metadata.tipos.TEXT;
        			break; 
        		}
        		unMeta.esPrimaryKey = findResultSet(pKs, "COLUMN_NAME", unMeta.id);
        		unMeta.catalogName=rsmd.getCatalogName(unMeta.columnId);
        		unMeta.tableName=rsmd.getTableName(unMeta.columnId);
        		if (this.ListaCamposOriginal != null) {
        			unMeta.originalColumnText = this.ListaCamposOriginal[i-1].trim();
            		if (!("").equals(unMeta.tableName)) {
            			// en ese caso se trata de un campo directo de la tabla, por lo que debemos asegurar que ID contiene el nombre del campo original para evitar errores.
            			String array[] = unMeta.originalColumnText.split("\\.");
            			unMeta.id = array[array.length-1]; 
            		}
        		}else {
        			unMeta.originalColumnText=unMeta.id;
        		}
    			// ahora se tendria que buscar de forma recursiva todas las dependencias
        		obtenerDependencias(c, unMeta, rsmd, dbmd);
        		metadataList.add(unMeta);
			}
		}catch(Exception ex) {
			System.out.println("ERROR on setMetadata,  " + ex.getMessage());
		}
	}
	private boolean findResultSet(ResultSet rs, String columna, String valor) {
		Boolean result = false;
		try {
			int i=0;
			i = rs.findColumn(columna);
			for(rs.first(); true ;rs.next()) {
				result = rs.getString(i).equals(valor);
				if (result) break;
				if (rs.isLast()) break;
			}
		}catch(Exception ex) {
			
		}
		return result;
	}
	public Metadata getPrimaryKeyMeta() {
		Metadata elMeta = metadataList.stream()
				  .filter(unMeta -> (unMeta.esPrimaryKey))
				  .findAny()
				  .orElse(null);		
		
		return elMeta;
	}
	public Metadata getMetadata(String columnName) {
		Metadata elMeta = metadataList.stream()
				  .filter(unMeta -> columnName.equalsIgnoreCase(unMeta.id))
				  .findAny()
				  .orElse(null);		
		if (elMeta == null) {
			// si no lo encuentra por id lo buscaremos por alias
			elMeta = metadataList.stream()
					  .filter(unMeta -> columnName.equalsIgnoreCase(unMeta.label))
					  .findAny()
					  .orElse(null);		
		}
		return elMeta;
	}
	private void obtenerDependencias(Connection c, Metadata unMeta, ResultSetMetaData rsmd, DatabaseMetaData dbmd) {
		try {
			ResultSet rs=null;
			if (rsDependencias==null) {
				rsDependencias = new HashMap<String, ResultSet>();
			}
			if (!rsDependencias.containsKey(unMeta.tableName)) {
				rsDependencias.put(unMeta.tableName,dbmd.getCrossReference(unMeta.catalogName, null, null,  null, null, unMeta.tableName));
			}
			rs = rsDependencias.get(unMeta.tableName);
			rs.beforeFirst();
			String dato="";
            while(rs.next()) {
            	dato = rs.getString("FKCOLUMN_NAME");
            	if (unMeta.id.equals(dato)) {
            		unMeta.type=Metadata.tipos.COMPLEX;
            		unMeta.esForeignKey = true;
            		String sql = "";
            		unMeta.pkColumnName = rs.getString("PKCOLUMN_NAME");
            		unMeta.pkTableName = rs.getString("PKTABLE_NAME");
            		sql = "Select * from " + unMeta.pkTableName + " LIMIT 1";  // limitimaos los resultados para que demore la consulta cuando las tablas son muy grandes
            		Statement stmt = c.createStatement();
        	        ResultSet rsDep = stmt.executeQuery(sql);
        	        ResultSetMetaData rsmdDep = rsDep.getMetaData();
        	        unMeta.metadataDep = new metaDatas();
        	        unMeta.metadataDep.setMetadata(c,sql,rsmdDep,dbmd);
	        	}
            }
		}catch(Exception ex) {
			System.out.println("ERROR on obtenerDependencias,  " + ex.getMessage() + ex.toString());
		}
	}
	String JSresult;
	public String getJSon() {
		JSresult="[";
		try {
			metadataList.forEach((v)-> {
				JSresult+=v.getJSon()+",";
			   }
			);
	        if (JSresult.length()>2) {
	        	JSresult = JSresult.substring( 0, JSresult.length()-1); // quitamos la última coma
	        }
			JSresult+="]";
		}catch (Exception ex) {
			
		}
		return JSresult;
	}
	String jsRresultSQL;
	public String getCompleteSQL(ReferenceInt nodoPath) {
		jsRresultSQL="";
		try {
			metadataList.forEach((v)-> {
				jsRresultSQL+=v.getCompleteSQL(nodoPath);
			   }
			);
	        if (jsRresultSQL.length()>2) {
	        	jsRresultSQL = jsRresultSQL.substring( 0, jsRresultSQL.length()-1); // quitamos la última coma
	        }
		}catch (Exception ex) {
			
		}
		return jsRresultSQL;
	}
	public String getCompleteSQLFROM() {
		jsRresultSQL="";
		try {
			metadataList.forEach((v)-> {
				jsRresultSQL+=v.getCompleteSQLFROM();
			});
		}catch (Exception ex) {
			
		}
		return jsRresultSQL;
	}
	String getAllFromDependenciesData="";
	private String getAllFromDependencies() {
		try {
			rsDependencias.forEach(( unTableName, rs)->{
				if (!("").equals(unTableName)) {
					try {
						rs.beforeFirst();
						getAllFromDependenciesData="";
			            while(rs.next()) {
			            	getAllFromDependenciesData+= unTableName + "," + rs.getString("PKTABLE_NAME") + ",";
			            }
					}catch(Exception ex) {
						
					}
				}
			});
		}catch(Exception ex) {
			System.out.println("ERROR on obtenerDependencias,  " + ex.getMessage() + ex.toString());
		}
		return getAllFromDependenciesData;
	}
	public String getDistinctSQLFROM() {
		String jsRresultSQL;
		jsRresultSQL=getCompleteSQLFROM();
		jsRresultSQL+=getAllFromDependencies();
		if (jsRresultSQL.length()>2) {
        	jsRresultSQL = jsRresultSQL.substring( 0, jsRresultSQL.length()-1); // quitamos la última coma
        }
        if(("").equals(jsRresultSQL)) {
        	jsRresultSQL=fromClauseOriginal;
        }else {
        	jsRresultSQL+= ", " + fromClauseOriginal;
        }
		int i;
		try {
			String[] array = jsRresultSQL.split(",", -1);
			java.util.Set<String> hash_Set = new java.util.HashSet<String>();
			for (i=0; i< array.length;i++) {
				hash_Set.add(array[i].trim().toLowerCase());
			}
			jsRresultSQL=hash_Set.toString();
			jsRresultSQL=jsRresultSQL.replaceAll("\\[", "");
			jsRresultSQL=jsRresultSQL.replaceAll("\\]", "");
		}catch (Exception ex) {
			
		}
		return jsRresultSQL;
	}
	public String getCompleteSQLWHERE() {
		jsRresultSQL="";
		try {
			metadataList.forEach((v)-> {
				jsRresultSQL+=v.getCompleteSQLWHERE();
			});
		}catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return jsRresultSQL;
	}
	String getAllWhereDependenciesData="";
	private String getAllWhereDependencies() {
		try {
			rsDependencias.forEach(( unTableName, rs)->{
				if (!("").equals(unTableName)) {
					try {
						rs.beforeFirst();
						getAllWhereDependenciesData="";
			            while(rs.next()) {
			            	getAllWhereDependenciesData+= unTableName + "." + rs.getString("FKCOLUMN_NAME") + " = " + rs.getString("PKTABLE_NAME") + "." + rs.getString("PKCOLUMN_NAME") + " and ";
			            }
					}catch(Exception ex) {
						
					}
				}
			});
		}catch(Exception ex) {
			System.out.println("ERROR on obtenerDependencias,  " + ex.getMessage() + ex.toString());
		}
		return getAllWhereDependenciesData;
	}
	public String getDistinctSQLWHERE() {
		String jsRresultSQL;
		jsRresultSQL=getCompleteSQLWHERE();
		jsRresultSQL+=getAllWhereDependencies();
        if (jsRresultSQL.length()>2) {
        	jsRresultSQL = jsRresultSQL.substring( 0, jsRresultSQL.length()-5); // quitamos el último AND
        }
		int i;
		try {
			String[] array = jsRresultSQL.split("(?i) AND ", -1);
			java.util.Set<String> hash_Set = new java.util.HashSet<String>();
			for (i=0; i< array.length;i++) {
				hash_Set.add(array[i].trim().toLowerCase());
			}
			//jsRresultSQL=hash_Set.toString();
	        Iterator<String> myIter = hash_Set.iterator(); 
	        jsRresultSQL="";
	        while (myIter.hasNext())
	        	jsRresultSQL+=myIter.next() + " and "; 
	        if (jsRresultSQL.length()>2) {
	        	jsRresultSQL = jsRresultSQL.substring( 0, jsRresultSQL.length()-5); // quitamos el último AND
	        }
		}catch (Exception ex) {
			
		}
		return jsRresultSQL;
	}
	String rellenaSubDataStr="";
    public String rellenaSubData(ResultSet rs2) {
    	rellenaSubDataStr="";
		try {
			rellenaSubDataStr += "{";
			metadataList.forEach((unMeta)-> {
				rellenaSubDataStr += "\"" + unMeta.id + "\":";
				try {
					switch(unMeta.type) {
					case COMPLEX:
						String unSubResult=unMeta.metadataDep.rellenaSubData(rs2);
						if (!("").equals(unSubResult)) {
							rellenaSubDataStr += unSubResult;
						}else {
							rellenaSubDataStr += "0";
						}
						break;
					case INTEGER:
						rellenaSubDataStr += "" + rs2.getInt(unMeta.currentField_id);
						break;
					case LONG:
						rellenaSubDataStr += "" + rs2.getLong(unMeta.currentField_id);
						break;
					case BOOLEAN:
						rellenaSubDataStr += "" + rs2.getBoolean(unMeta.currentField_id);
						break;
					case DOUBLE:
						rellenaSubDataStr += "" + rs2.getDouble(unMeta.currentField_id);
						break;
					case DATE:
							rellenaSubDataStr += "\"" + rs2.getDate(unMeta.currentField_id) + "\"";
						break;
					case TEXT:
							rellenaSubDataStr += "" + unMeta.getQuoteString("" + rs2.getString(unMeta.currentField_id)) + "";
						break;
					default:
						rellenaSubDataStr += "\"" + rs2.getString(unMeta.currentField_id) + "\"";
						break;
					
					}
					rellenaSubDataStr += ",";
				} catch (SQLException e) {
					e.printStackTrace();
				}
			});
			if(rellenaSubDataStr.length()>2) {
				rellenaSubDataStr=rellenaSubDataStr.substring(0, rellenaSubDataStr.length()-1);
			}
			rellenaSubDataStr += "}";
		}catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return rellenaSubDataStr;
    }
    private String tmpSQL="";
	public String sustituyetableAliases(String sql) {
		// TODO Auto-generated method stub
		tmpSQL=sql;
		if (metaTableList.size()>0) {
			fromClauseOriginal=""; // lo vamos a recomponer pero sin los aliases
		}
		metaTableList.forEach((v)-> {
			tmpSQL=v.sustituyeAlias(tmpSQL);
			fromClauseOriginal+= v.tableName + ", ";
		});
		if (metaTableList.size()>0) {
			fromClauseOriginal=fromClauseOriginal.substring(0, fromClauseOriginal.length()-1); // quitamos la última coma
		}
		//fromClauseOriginal
		return tmpSQL;
	}


}
