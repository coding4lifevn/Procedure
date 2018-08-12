package org.vgu.sql;

public interface Visitor {
	public String visit(SQLCreate sqlCreate);
	public String visit(SQLSelect sqlSelect);
	public String visit(SQLJoin sqlJoin);
}
