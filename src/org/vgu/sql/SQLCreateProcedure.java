package org.vgu.sql;

import java.util.Iterator;

public class SQLCreateProcedure extends SQLCreateFunction {
	@Override
	public String toString() {
		String s = "";
		s += "CREATE PROCEDURE " + name + "(" + parameters +")\n";
		if (returnType != null) {
			s+= "RETURN " + returnType;
		}
		s += "\nBEGIN\n";
		if (declareVariables != null) {
			s+= "DECLARE " + declareVariables + "\n";
		}
		if (setVariables != null) {
			s+= setVariables + "\n";
		}
		if (returnValue != null) {
			s+= "RETURN " + returnValue + "\n";
		}
		if (body != null) {
			int i = 0;
			while (i < body.size()) {
				if (i+1 == body.size()) {
					s+= body.get(i);
				}else {
					s+= body.get(i) + "\n";
				}				
				i++;
			}
		}
		if (ifclause != null) {
			s+= ifclause + "\n";
		}
		s += "\nEND\n";
		return s;
	}
}
