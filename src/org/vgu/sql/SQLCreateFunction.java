package org.vgu.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SQLCreateFunction {
	protected String name;
	protected String parameters;
	protected IfClause ifclause;
	protected String returnType;
	protected String setVariables;
	protected String declareVariables;
	protected String returnValue;
	protected List<String> body = new ArrayList<String>();
	
	public List<String> getBody() {
		return body;
	}
	public void setBody(List<String> body) {
		this.body = body;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public String getSetVariables() {
		return setVariables;
	}
	public void setSetVariables(String setVariables) {
		this.setVariables = setVariables;
	}
	public String getDeclareVariables() {
		return declareVariables;
	}
	public void setDeclareVariables(String declareVariables) {
		this.declareVariables = declareVariables;
	}
	public String getReturnValue() {
		return returnValue;
	}
	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}
	
	public IfClause getIfclause() {
		return ifclause;
	}
	public void setIfclause(IfClause ifclause) {
		this.ifclause = ifclause;
	}
	
	
	@Override
	public String toString() {
		String s = "";
		s += "CREATE FUNCTION " + name + "(" + parameters +")\n";
		if (returnType != null) {
			s+= "RETURN " + returnType;
		}
		s += "\nBEGIN\n";
		if (declareVariables != null) {
			s+= declareVariables + "\n";
		}
		if (setVariables != null) {
			s+= setVariables + "\n";
		}
		if (returnValue != null) {
			s+= "RETURN " + returnValue + "\n";
		}
		
		if (ifclause != null) {
			s+= ifclause + "\n";
		}
		s += "END\n";
		return s;
	}
	
}
