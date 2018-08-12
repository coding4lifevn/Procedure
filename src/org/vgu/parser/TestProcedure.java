package org.vgu.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.vgu.sql.ElseClause;
import org.vgu.sql.ExceptionMsg;
import org.vgu.sql.IfClause;
import org.vgu.sql.SQLCreateFunction;
import org.vgu.sql.SQLCreateProcedure;
import org.vgu.sql.ThenClause;

public class TestProcedure {
	
	/*Function for the creation of an SQL function*/
	public static SQLCreateFunction createSQL(JSONArray policy, int role, String property) {
		Iterator i = policy.iterator();
		SQLCreateFunction function = new SQLCreateFunction();
		while(i.hasNext()) {
			JSONObject object = (JSONObject) i.next();
			Long integer = (long) role;
			if(object.get("role").equals(integer) && object.get("property").equals(property)) {
				switch (role) {
				case 1:
					function.setName("auth_read_name_admin");
					break;
				case 2:
					function.setName("auth_read_name_lecturer");
					break;
				case 3:
					function.setName("auth_read_name_student");
					break;
				default:
					break;
				}
				function.setParameters("selft_id int, caller_id int");
				function.setDeclareVariables("var=INTEGER");
				function.setReturnType("INTEGER");
				function.setReturnValue("var");
				if(object.get("check").equals(true)) {
					function.setSetVariables("var=1");
				} else {
					function.setSetVariables("var=0");
				}
			}
		}
		return function;
	}
	
	/*This function is used to create the auth_read_name function*/
	public static SQLCreateFunction newSQLFunction() {
		
		ExceptionMsg exMsg = new ExceptionMsg();
		exMsg.setMsgText("Security exception");
		exMsg.setSignalNo("4500");
		
		IfClause if1 = new IfClause();
		if1.setCondition("caller_role=2");
		ThenClause then1 = new ThenClause();
		then1.setReturnValue("1");
		
		IfClause subif1 = new IfClause();
		subif1.setCondition("auth_read_name_lecturer(self_id, caller_id)");
		
		ThenClause subthen1 = new ThenClause();
		subthen1.setReturnValue("1");
		subif1.setThen(subthen1);
		ElseClause subElse1 = new ElseClause();
		subElse1.setExMsg(exMsg);
		subElse1.setReturnValue("0");
		subif1.setElseclause(subElse1);
		then1.setIf(subif1);
		if1.setThen(then1);
		
		ElseClause else1 = new ElseClause();
		IfClause if2 = new IfClause();
		if2.setCondition("caller_role = 3");
		ThenClause then2 = new ThenClause();
		
		IfClause subif2 = new IfClause();
		subif2.setCondition("auth_read_name_student(self_id, caller_id)");
		ThenClause subthen2 = new ThenClause();
		subthen2.setReturnValue("1");
		ElseClause subElse2 = new ElseClause();
		subElse2.setExMsg(exMsg);
		subElse2.setReturnValue("0");
		subif2.setThen(subthen2);
		subif2.setElseclause(subElse2);
		then2.setIf(subif2);
		if2.setThen(then2);
		else1.setIfclause(if2);
		if1.setElseclause(else1);
		
		
		ElseClause else2 = new ElseClause();
		IfClause if3 = new IfClause();
		if3.setCondition("caller_role = 1");
		ThenClause then3 = new ThenClause();
		
		IfClause subif3 = new IfClause();
		subif3.setCondition("auth_read_name_student(self_id, caller_id)");
		ThenClause subthen3 = new ThenClause();
		subthen3.setReturnValue("1");
		ElseClause subElse3 = new ElseClause();
		subElse3.setExMsg(exMsg);
		subElse3.setReturnValue("0");
		subif3.setThen(subthen3);
		subif3.setElseclause(subElse3);
		then3.setIf(subif3);
		if3.setThen(then3);
		else2.setIfclause(if3);
		if2.setElseclause(else2);

		ElseClause else3 = new ElseClause();
		else3.setExMsg(exMsg);
		else3.setReturnValue("0");
		if3.setElseclause(else3);
		
		SQLCreateFunction create1 = new SQLCreateFunction();
		create1.setName("auth_read_name");
		create1.setParameters("self_id int, caller_id int, caller_role int");
		create1.setReturnType("INT DETERMINISTIC");
		create1.setIfclause(if1);		
		return create1;
	}
	
	public SQLCreateProcedure createCustomProcedure(PlainSelect plainSelect, JSONArray policy) {
		SQLCreateProcedure procedure = new SQLCreateProcedure();
		procedure.setName("GetStudentListSec");
		procedure.setParameters("IN caller_id INT");
		procedure.setReturnType("INT DETERMINISTIC");
		procedure.setDeclareVariables("caller_role INT DEFAULT 0");
		
		/*Create first select statement*/
		PlainSelect roleSelect = new PlainSelect();
		SelectExpressionItem item = new SelectExpressionItem();
		
		Column role = new Column();
		role.setColumnName("role");
		item.setExpression(role);
		
		Column caller_role = new Column();
		caller_role.setColumnName("caller_role");
		
		Column caller_id = new Column();
		caller_id.setColumnName("caller_id");
		
		Table tab = new Table();
		tab.setName("caller_role");
		List<Table> tab_list = new ArrayList<Table>();
		tab_list.add(tab);
		roleSelect.addSelectItems(item);;
		roleSelect.setIntoTables(tab_list);
		roleSelect.setFromItem(plainSelect.getFromItem());
		roleSelect.setWhere(plainSelect.getJoins().get(0).getOnExpression());

		PlainSelect caseSelect = this.createSecondSelect(plainSelect, caller_id, caller_role);
		List<String> bodylist = new ArrayList<>();
		bodylist.add(roleSelect.toString());
		bodylist.add(caseSelect.toString());
		procedure.setBody(bodylist);

		return procedure;
	}
	
	public static void main(String[] args) throws JSQLParserException, FileNotFoundException, IOException, ParseException {
		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		String statement = "SELECT family_name, middle_name, given_name FROM reg_user\r\n" + 
				"		INNER JOIN student\r\n" + 
				"		ON reg_user_id = student_id;";
		Select select = (Select) parserManager.parse(new StringReader(statement));
		PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
		
		TestProcedure testProcedure = new TestProcedure();
		Object obj = new JSONParser().parse(new FileReader("C:\\\\Users\\\\HP_2\\\\Desktop\\\\MergeSourceThesis\\\\Critical Part\\\\policy.json"));
		JSONObject jsonObject = (JSONObject) obj;
		JSONArray jsonArray = (JSONArray) jsonObject.get("data");
		
		SQLCreateProcedure procedure = testProcedure.createCustomProcedure(plainSelect, jsonArray);
		
		System.out.println(TestProcedure.createSQL(jsonArray, 1, "given_name"));
		System.out.println(TestProcedure.createSQL(jsonArray, 2, "family_name"));
		System.out.println(TestProcedure.createSQL(jsonArray, 3, "middle_name"));
		
		System.out.println(newSQLFunction());
		System.out.println(procedure.toString());
	}
	
	public PlainSelect createSecondSelect(PlainSelect plainSelect, Column caller_id, Column caller_role) {
		PlainSelect caseSelect = new PlainSelect();
		caseSelect.setFromItem(plainSelect.getFromItem());
		caseSelect.setJoins(plainSelect.getJoins());
		
		/*List of cases*/
		List<SelectItem> list = new ArrayList<SelectItem>();
		SelectExpressionItem item1 = new SelectExpressionItem();
		SelectExpressionItem item2 = new SelectExpressionItem();
		SelectExpressionItem item3 = new SelectExpressionItem();
		CaseExpression case1 = new CaseExpression();
		CaseExpression case2 = new CaseExpression();
		CaseExpression case3 = new CaseExpression();
		
		
		SelectExpressionItem item_col1 = (SelectExpressionItem) plainSelect.getSelectItems().get(0);
		SelectExpressionItem item_col2 = (SelectExpressionItem) plainSelect.getSelectItems().get(1);
		SelectExpressionItem item_col3 = (SelectExpressionItem) plainSelect.getSelectItems().get(2);
		
		Column c1 = (Column) item_col1.getExpression();
		Column c2 = (Column) item_col2.getExpression();
		Column c3 = (Column) item_col3.getExpression();
		
		Alias name1 = new Alias(c1.getColumnName());
		Alias name2 = new Alias(c2.getColumnName());
		Alias name3 = new Alias(c3.getColumnName());
		
		item1.setAlias(name1);
		item2.setAlias(name2);
		item3.setAlias(name3);
		
		Function func1 = new Function();
		func1.setName("auth_read_name");
		List<Expression> column_list = new ArrayList<Expression>();
		EqualsTo temp = (EqualsTo) plainSelect.getJoins().get(0).getOnExpression();
		column_list.add((Column) temp.getLeftExpression());
		column_list.add(caller_id);
		column_list.add(caller_role);
		ExpressionList el1 = new ExpressionList();
		el1.setExpressions(column_list);
		func1.setParameters(el1);
		case1.setSwitchExpression(func1);
		WhenClause when = new WhenClause();
		LongValue val = new LongValue(1);
		c1.setTable((Table) plainSelect.getFromItem());
		when.setWhenExpression(val);
		when.setThenExpression(c1);
		List<WhenClause> clauses = new ArrayList<>();
		clauses.add(when);
		case1.setWhenClauses(clauses);
		item1.setExpression(case1);
		
		c2.setTable((Table) plainSelect.getFromItem());
		when.setThenExpression(c2);
		case2.setSwitchExpression(func1);
		case2.setWhenClauses(clauses);
		item2.setExpression(case2);
		
		c3.setTable((Table) plainSelect.getFromItem());
		when.setThenExpression(c3);
		case3.setSwitchExpression(func1);
		case3.setWhenClauses(clauses);
		item3.setExpression(case3);
		
		list.add(item1);
		list.add(item2);
		list.add(item3);
		caseSelect.setSelectItems(list);
		return caseSelect;
	}
}
