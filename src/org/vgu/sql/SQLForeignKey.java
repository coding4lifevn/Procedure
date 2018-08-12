package org.vgu.sql;

public class SQLForeignKey extends SQLColumn {
	private String references;
	private String refTable;

	public String getReferences() {
		return references;
	}
	public void setReferences(String references) {
		this.references = references;
	}
	public String getRefTable() {
		return refTable;
	}
	public void setRefTable(String refTable) {
		this.refTable = refTable;
	}
	
	
	
}
