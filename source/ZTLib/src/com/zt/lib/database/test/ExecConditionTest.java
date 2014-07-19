package com.zt.lib.database.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zt.lib.database.ExecCondition;

public class ExecConditionTest {

	ExecCondition execCondition;
	
	@Before
	public void setUp() throws Exception
	{
		execCondition = null;
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testBuild()
	{
		execCondition = ExecCondition.Build();
		assertNotNull(execCondition);
	}

	@Test
	public void testWhere()
	{
		execCondition = ExecCondition.Build().where("column1").equal(1).and()
				.where("column2").notEqual(2).done();
		String[] args = execCondition.getWhereArgs();
		assertEquals("column1 = ? AND column2 <> ? ", execCondition.getWhereClause());
		assertEquals(1 + "", args[0]);
		assertEquals(2 + "", args[1]);
	}

	@Test
	public void testLess()
	{
		execCondition = ExecCondition.Build().where("column1").less(1).and()
				.where("column1").less(5).done();
		String[] args = execCondition.getWhereArgs();
		assertEquals("column1 < ? AND column1 < ? ", execCondition.getWhereClause());
		assertEquals(1 + "", args[0]);
		assertEquals(5 + "", args[1]);
	}

	@Test
	public void testLessEqual()
	{
		execCondition = ExecCondition.Build().where("column1").lessEqual(1).done();
		String[] args = execCondition.getWhereArgs();
		assertEquals("column1 <= ? ", execCondition.getWhereClause());
		assertEquals(1 + "", args[0]);
	}

	@Test
	public void testEqual()
	{
		execCondition = ExecCondition.Build().where("column1").equal(true).done();
		String[] args = execCondition.getWhereArgs();
		assertEquals("column1 = ? ", execCondition.getWhereClause());
		assertEquals(1 + "", args[0]);
	}

	@Test
	public void testNotEqual()
	{
		execCondition = ExecCondition.Build().where("column1").notEqual(1).done();
		String[] args = execCondition.getWhereArgs();
		assertEquals("column1 <> ? ", execCondition.getWhereClause());
		assertEquals(1 + "", args[0]);
	}

	@Test
	public void testMoreEqual()
	{
		execCondition = ExecCondition.Build().where("column1").moreEqual(1).done();
		String[] args = execCondition.getWhereArgs();
		assertEquals("column1 >= ? ", execCondition.getWhereClause());
		assertEquals(1 + "", args[0]);
	}

	@Test
	public void testMore()
	{
		execCondition = ExecCondition.Build().where("column1").more(1).done();
		String[] args = execCondition.getWhereArgs();
		assertEquals("column1 > ? ", execCondition.getWhereClause());
		assertEquals(1 + "", args[0]);
	}

	@Test
	public void testBetween()
	{
		execCondition = ExecCondition.Build().where("column1").between(1, 5).and()
				.where("column2").between(6, 10).done();
		String[] args = execCondition.getWhereArgs();
		assertEquals("column1 BETWEEN ? AND ? AND column2 BETWEEN ? AND ? ", 
				execCondition.getWhereClause());
		assertEquals(1 + "", args[0]);
		assertEquals(5 + "", args[1]);
		assertEquals(6 + "", args[2]);
		assertEquals(10 + "", args[3]);
	}

	@Test
	public void testLike()
	{
		execCondition = ExecCondition.Build().where("column1").between(1, 5).and()
				.where("column2").like("ne%").done();
		String[] args = execCondition.getWhereArgs();
		assertEquals("column1 BETWEEN ? AND ? AND column2 LIKE '?' ", 
				execCondition.getWhereClause());
		assertEquals(1 + "", args[0]);
		assertEquals(5 + "", args[1]);
		assertEquals("ne%", args[2]);
	}

	@Test
	public void testOrderby()
	{
		execCondition = ExecCondition.Build().orderby("column1").and()
				.orderby("column2").done();
		assertEquals("column1 AND column2 ", execCondition.getOrderby());
	}

	@Test
	public void testAnd()
	{
		execCondition = ExecCondition.Build().and().orderby("column1").done();
		System.out.println(execCondition.getOrderby());
		assertEquals("column1 ", execCondition.getOrderby());
	}

	@Test
	public void testDone()
	{
		execCondition = ExecCondition.Build().done();
		assertNull(execCondition.getOrderby());
		assertNull(execCondition.getWhereArgs());
		assertNull(execCondition.getWhereClause());
	}

	@Test
	public void testGetWhereClause()
	{
		execCondition = ExecCondition.Build().and().done();
		assertNull(execCondition.getWhereClause());
	}

	@Test
	public void testGetWhereArgs()
	{
		execCondition = ExecCondition.Build().and().done();
		assertNull(execCondition.getWhereArgs());
	}

	@Test
	public void testGetOrderby()
	{
		execCondition = ExecCondition.Build().and().done();
		assertNull(execCondition.getOrderby());
	}

}
