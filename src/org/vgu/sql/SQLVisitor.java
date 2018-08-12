package org.vgu.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vgu.uml.UMLAssociation;
import org.vgu.uml.UMLAttribute;
import org.vgu.uml.UMLClass;
import org.vgu.uml.UMLContext;
import org.vgu.uml.UMLContextExample;
public class SQLVisitor implements Visitor {
	
	private SQLTable surrogateTable = null;
	private SQLTable tempTable = null;
	private SQLTable sqlTable;
	private List<SQLTable> listSQLTable = null;
	//public List<SQLTable> listSQLTable;
	private ArrayList<String> value = null;
	
	public SQLTable getSqlTable() {
		return sqlTable;
	}

	public void setSqlTable(SQLTable sqlTable) {
		this.sqlTable = sqlTable;
	}

	@Override
	public String visit(SQLCreate sqlCreate) {
		sqlTable = this.createSQLTable(sqlCreate.getUMLContext());
		String str = "";
		String s = "";
		for(SQLColumn c:sqlTable.getColumns()) {
			str+= c.getName() + " " + c.getType() + " " + c.getProperties() + ",\n";
		}
		if(!sqlTable.getForeignKeys().isEmpty()) {
			str += "PRIMARY KEY (" + sqlTable.getPrimaryKey().getName() + "),\n";
			for(SQLForeignKey f:sqlTable.getForeignKeys()) {
				str += "FOREIGN KEY (" + f.getName()+")";
			}
		}
		else {
			str += "PRIMARY KEY (" + sqlTable.getPrimaryKey().getName() + ")";
		}
		s = "CREATE TABLE " + sqlTable.getName() + "(\n" + str + "\n);";
		if(surrogateTable==null) {
			return s;
		}
		tempTable = surrogateTable;
		surrogateTable = null;
		return s;
	}

	@Override
	public String visit(SQLSelect sqlSelect) {
		// TODO Auto-generated method stub
		SQLTable selectTable = new SQLTable();
		selectTable = this.createSQLTable(sqlSelect.getUMLContext());
		String str = "";
		//str += "SELECT * FROM " + sqlSelect.getUMLContext().getClasses().getName() + addComma();
		str += "SELECT * FROM " + selectTable.getName() + addSemiColon();
		for(SQLColumn u:selectTable.getColumns()) {
			str += "SELECT " + u.getName() + " FROM "
					+ selectTable.getName() + addSemiColon();
		}
		return str;
	}
	
	@Override
	public String visit(SQLJoin sqlJoin) {
		// TODO Auto-generated method stub
		List<SQLTable> joinTableList;// = null;
		joinTableList = this.createListSQLTable(sqlJoin.getSchema());
		String str = "";
		Map<String, ArrayList<String>> column = new HashMap<String, ArrayList<String>>();
		for(SQLTable c: joinTableList) {
			String key = c.getName();
			value = new ArrayList<String>();
			//System.out.println(key);
			for (SQLColumn u: c.getColumns()) {
				value.add(u.getName());
				//System.out.println(u.getName());
			}
	//		System.out.println(c.getColumns().size());
			column.put(key, value);
		}
		for(SQLTable c: joinTableList) {
			for (SQLForeignKey b : c.getForeignKeys()) {
				String refer = b.getReferences();
				String currentColumns = c.getName();
				String FK = "";
				//System.out.println("day la" + currentColumns);
				//List<String> columns = column.get(refer);
				str += "SELECT ";
				for (String name: column.get(currentColumns)) {
					if (name.equals(refer + "ID")) {
						FK = name;
						continue;
					}
					str += currentColumns + "." + name + ",";
				}
				int index = 0;
				for (String name: column.get(refer)) {
					index++;
					if (name.equals(refer + "ID")) {
						continue;
					}
					if (index == column.get(refer).size())
					{
						str += refer + "." + name ;
					}else {
						str += refer + "." + name + ",";
					}
					
				}
				str += "\nFROM " + currentColumns + " INNER JOIN " + refer + " ON "
						+ currentColumns + "." + FK + " = " + refer + "." + FK + "\n\n";
			}
		}		
		return str;
	}
	
	private static String addComma() {
		return ",\n";
	}
	
	private static String addSemiColon() {
		return ";\n";
	}
	
	private List<SQLForeignKey> createForeignKey(UMLContext umlContext) {
		List<SQLForeignKey> sqlForeignKeys = new ArrayList<SQLForeignKey>();
		for(UMLAssociation a:umlContext.getAssociations()) {
			SQLForeignKey tempt = new SQLForeignKey();
			if (umlContext.areBothEndsMulti(a)) {
				if(!a.isCheck()) {
					surrogateTable = new SQLTable();
					String u = a.getSource().getTarget().getName();
					String v = a.getSource().getSource().getName();
					SQLForeignKey c1 = new SQLForeignKey();
					SQLForeignKey c2 = new SQLForeignKey();
					c1.setName(u+"ID");
					c1.setType("INT");
					c1.setProperties("UNIQE NOT NULL");
					c2.setName(v+"ID");
					c2.setType("INT");
					c2.setProperties("UNIQUE NOT NULL");
					c1.setReferences(u);
					c2.setReferences(v);
					List<SQLForeignKey> col = new ArrayList<SQLForeignKey>();
					col.add(c1);
					col.add(c2);
					
					surrogateTable.setName(u+"_"+v);
					surrogateTable.setForeignKeys(col);
				}
				a.setCheck(true);
			}
			else if(umlContext.areBothEndsMixed(a)) {
				if(umlContext.isMulti(a.getTarget())) {
					if(a.getTarget().getTarget().equals(umlContext.getClasses())) {
						tempt.setName(a.getTarget().getSource().getName() + "ID");
						tempt.setProperties("NOT NULL");
						tempt.setReferences(a.getTarget().getSource().getName());
						sqlForeignKeys.add(tempt);
					}
				}
				else if(umlContext.isMulti(a.getSource())) {
					
					if(a.getSource().getTarget().equals(umlContext.getClasses())) {
						tempt.setName(a.getSource().getSource().getName() + "ID");
						tempt.setProperties("NOT NULL");
						tempt.setReferences(a.getSource().getSource().getName());
						sqlForeignKeys.add(tempt);
					}
				}
			}  else if(umlContext.areBothEndsAtMostOne(a)) {
				if(a.getSource().getTarget().equals(umlContext.getClasses())) {
					String s = a.getSource().getSource().getName()+ "ID";
					tempt.setName(s);
					tempt.setProperties("UNIQUE");
					tempt.setReferences(s);
					sqlForeignKeys.add(tempt);
				}
			}
		}
		return sqlForeignKeys;
	}

	@SuppressWarnings("null")
	private List<SQLTable> createListSQLTable(Map<Integer, UMLContext> schema){
		listSQLTable = new ArrayList<SQLTable>();
		for (UMLContext value : schema.values()) {
			sqlTable = new SQLTable();
			SQLColumn primaryKey = new SQLColumn();
			List<SQLColumn> sqlColumn = new ArrayList<SQLColumn>();
			List<SQLForeignKey> sqlForeignKeys = new ArrayList<SQLForeignKey>();
			sqlTable.setName(value.getClasses().getName());
			primaryKey.setName(value.getClasses().getName()+"ID");
			primaryKey.setType("int");
			primaryKey.setProperties("NOT NULL AUTO INCREMENT");
			sqlColumn.add(primaryKey);
			sqlTable.setPrimaryKey(primaryKey);
			
			for(UMLAttribute u:value.getAttributes()) {
				SQLColumn col = new SQLColumn();
				col.setName(u.getName());
				col.setType(u.getType());
				col.setProperties("NOT NULL");
				sqlColumn.add(col);
			}
			sqlForeignKeys = this.createForeignKey(value);
			for(SQLColumn s:sqlForeignKeys) {
					sqlColumn.add(s);
			}
			sqlTable.setColumns(sqlColumn);
			sqlTable.setForeignKeys(sqlForeignKeys);
			listSQLTable.add(sqlTable);
		}
		return listSQLTable;
	}
	
	public SQLTable createSQLTable(UMLContext umlContext) {
		sqlTable = new SQLTable();
		SQLColumn primaryKey = new SQLColumn();
		List<SQLColumn> sqlColumn = new ArrayList<SQLColumn>();
		List<SQLForeignKey> sqlForeignKeys = new ArrayList<SQLForeignKey>();
		
		sqlTable.setName(umlContext.getClasses().getName());
		primaryKey.setName(umlContext.getClasses().getName()+"ID");
		primaryKey.setType("int");
		primaryKey.setProperties("NOT NULL AUTO INCREMENT");
		sqlColumn.add(primaryKey);
		sqlTable.setPrimaryKey(primaryKey);
		
		for(UMLAttribute u:umlContext.getAttributes()) {
			SQLColumn col = new SQLColumn();
			col.setName(u.getName());
			col.setType(u.getType());
			col.setProperties("NOT NULL");
			sqlColumn.add(col);
		}
		sqlForeignKeys = this.createForeignKey(umlContext);
		for(SQLColumn s:sqlForeignKeys) {
				sqlColumn.add(s);
		}
		sqlTable.setColumns(sqlColumn);
		sqlTable.setForeignKeys(sqlForeignKeys);
		return sqlTable;
	}

	public SQLTable getSurrogateTable() {
		return surrogateTable;
	}

	public void setSurrogateTable(SQLTable surrogateTable) {
		this.surrogateTable = surrogateTable;
	}

}
