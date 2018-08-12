package org.vgu.sql;

import org.vgu.uml.UMLContext;

public class SQLCreate implements SQLStatements {

	private UMLContext UMLContext;
	
	
	public UMLContext getUMLContext() {
		return UMLContext;
	}


	public void setUMLContext(UMLContext uMLContext) {
		UMLContext = uMLContext;
	}
	
	@Override
	public String accept(Visitor visitor) {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}

}
