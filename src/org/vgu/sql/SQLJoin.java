package org.vgu.sql;

import java.util.Map;

import org.vgu.uml.UMLContext;

public class SQLJoin implements SQLStatements{

	Map<Integer, UMLContext> schema;
    private SQLTable itemLeft;
    private  SQLTable itemRight;
	
	public Map<Integer, UMLContext> getSchema() {
		return schema;
	}


	public void setSchema(Map<Integer, UMLContext> schema) {
		this.schema = schema;
	}
	
	public SQLTable getItemRight() {
		return itemRight;
	}


	public void setItemRight(SQLTable itemRight) {
		this.itemRight = itemRight;
	}


	public SQLTable getItemLeft() {
		return itemLeft;
	}


	public void setItemLeft(SQLTable itemLeft) {
		this.itemLeft = itemLeft;
	}
	
	@Override
	public String accept(Visitor visitor) {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}

}
