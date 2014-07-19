package com.zt.lib.database;

import java.util.ArrayList;
import java.util.List;


public class ExecCondition {
	
	private static class Where {
		private String column;
		private EnumCondition condition;
		private List<String> args;
		private EnumArgType argType;
		
		enum EnumArgType {
			NUMBER,
			TEXT;
		}
		
		enum EnumCondition {
			LESS(" < "),
			LESS_EQUAL(" <= "),
			EQUAL(" = "),
			MORE_EQUAL(" >= "),
			MORE(" > "),
			NOT_EQUAL(" <> "),
			BETWEEN(" BETWEEN "),
			LIKE(" LIKE ");
			
			private String value;
			private EnumCondition(String value) {
				this.value = value;
			}
		}
		
	}
	
	private static class Orderby {
		private String column;
	}
	
	private static final String PLACE_HOLDER_NUMBER = "? ";
	private static final String AND = "AND ";
	private List<Where> mWheres;
	private List<Orderby> mOrderbys;
	private Where where;
	private Orderby orderby;

	public static ExecCondition Build() {
		return new ExecCondition();
	}
	
	public ExecCondition() {
		mWheres = new ArrayList<ExecCondition.Where>();
		mOrderbys = new ArrayList<ExecCondition.Orderby>();
	}
	
	public ExecCondition where(String column) {
		where = new Where();
		where.column = column;
		return this;
	}
	
	public <T> ExecCondition less(T arg) {
		if (null == where) throw new NullPointerException();
		where.condition = Where.EnumCondition.LESS;
		putArg(arg);
		return this;
	}
	
	public <T> ExecCondition lessEqual(T arg) {
		if (null == where) throw new NullPointerException();
		where.condition = Where.EnumCondition.LESS_EQUAL;
		putArg(arg);
		return this;
	}
	
	public <T> ExecCondition equal(T arg) {
		if (null == where) throw new NullPointerException();
		where.condition = Where.EnumCondition.EQUAL;
		putArg(arg);
		return this;
	}
	
	public <T> ExecCondition notEqual(T arg) {
		if (null == where) throw new NullPointerException();
		where.condition = Where.EnumCondition.NOT_EQUAL;
		putArg(arg);
		return this;
	}
	
	public <T> ExecCondition moreEqual(T arg) {
		if (null == where) throw new NullPointerException();
		where.condition = Where.EnumCondition.MORE_EQUAL;
		putArg(arg);
		return this;
	}
	
	public <T> ExecCondition more(T arg) {
		if (null == where) throw new NullPointerException();
		where.condition = Where.EnumCondition.MORE;
		putArg(arg);
		return this;
	}
	
	public <T> ExecCondition between(T min, T max) {
		if (null == where) throw new NullPointerException();
		where.condition = Where.EnumCondition.BETWEEN;
		putArg(min, max);
		return this;
	}
	
	public <T> ExecCondition like(T pattern) {
		if (null == where) throw new NullPointerException();
		where.condition = Where.EnumCondition.LIKE;
		putArg(pattern);
		return this;
	}
	
	private <T> void putArg(T... args)
	{
		if (args[0] instanceof String) {
			where.argType = Where.EnumArgType.TEXT;
		} else {
			where.argType = Where.EnumArgType.NUMBER;
		}
		where.args = new ArrayList<String>();
		for (T arg : args) {
			where.args.add(arg.toString());
		}
	}
	
	public ExecCondition orderby(String column) {
		orderby = new Orderby();
		orderby.column = column;
		return this;
	}
	
	public ExecCondition and() {
		return done();
	}
	
	public ExecCondition done() {
		if (null != where) {
			mWheres.add(where);
		}
		if (null != orderby) {
			mOrderbys.add(orderby);
		}
		return this;
	}
	
	public String[] getWhereFields() {
		final int size = mWheres.size();
		String[] result = new String[size];
		int index = 0;
		for (Where where : mWheres) {
			result[index] = where.column;
			index ++;
		}
		return result;
	}
	
	public void setWhereColumns(String[] names) {
		int index = 0;
		for (Where where : mWheres) {
			where.column = names[index];
			index ++;
		}
	}
	
	public String getWhereClause() {
		StringBuilder sb = new StringBuilder();			
		for (Where where : mWheres) {
			sb.append(where.column);
			sb.append(where.condition.value);
			int argNumber = 0;
			if (Where.EnumCondition.BETWEEN == where.condition) {
				argNumber = 2;
			} else {
				argNumber = 1;
			}
			for (int i = 0; i < argNumber; i ++) {
				sb.append(PLACE_HOLDER_NUMBER);
				sb.append(AND);
			}
		}
		int lastAndIndex = sb.lastIndexOf(AND);
		String result = null;
		if (-1 != lastAndIndex) {
			result = sb.substring(0, lastAndIndex).toString();
		}
		return result;
	}
	
	public String[] getWhereArgs() {
		List<String> args = new ArrayList<String>();
		for (Where where : mWheres) {
			for (String arg : where.args) {
				// if arg is boolean and type is number
				// we make true -> 1 & false -> 0
				if (Where.EnumArgType.NUMBER == where.argType) {
					if ("true".equals(arg) || "false".equals(arg)) {
						arg = (Boolean.valueOf(arg) ? 1 : 0) + "";
					}
				}
				args.add(arg);
			}
		}
		String[] result = null;
		if (!args.isEmpty()) {
			result = args.toArray(new String[args.size()]);
		}
		return result;
	}
	
	public String getOrderby() {
		StringBuilder sb = new StringBuilder();	
		for (Orderby orderby : mOrderbys) {
			sb.append(orderby.column);
			sb.append(" ");
			sb.append(AND);
		}
		int lastAndIndex = sb.lastIndexOf(AND);
		String result = null;
		if (-1 != lastAndIndex) {
			result = sb.substring(0, lastAndIndex).toString();
		}
		return result;
	}
	
}
