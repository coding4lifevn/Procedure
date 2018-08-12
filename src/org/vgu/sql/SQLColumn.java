package org.vgu.sql;

public class SQLColumn {
	private String name;
	private String type;
	private String properties;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getProperties() {
		return properties;
	}
	public void setProperties(String properties) {
		this.properties = properties;
	}
	
	public String toSQLString() {
		return this.getName() + " " + this.getType() + " " + this.getProperties();
	}
	
	
}
