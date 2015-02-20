package org.disl.test

import groovy.sql.Sql

import org.disl.db.hsqldb.HsqldbSchema
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AssertSQL {
	
	static Sql sql
	
	protected static Sql getSql() {
		if (sql==null) {
			sql=new HsqldbSchema().getSql()
			sql.execute("CREATE TABLE DUAL (dummy char(1))")
			sql.execute("INSERT INTO DUAL VALUES ('X')")			
		}
		sql
	}
	
	public static void assertTrue(expression) {
		assertRowCount(1, "select 1 from dual where ${expression}")
	}
	
	@Test
	public void testAssertTrue() {
		assertTrue("1=1")
	}
	
	public static void assertRowCount(int expectedCount,String sqlQuery) {
		int actualCount=getRowCount(sqlQuery)
		Assert.assertEquals("""Invalid rowcount returned from query:
${sqlQuery}
""",expectedCount,actualCount,0)		
	}
	
	public static int getRowCount(String sqlQuery) {
		getSql().firstRow("select count(1) from (${sqlQuery})".toString()).find().value
	}
	
	@Test
	public void testAssertRowCount() {
		assertRowCount(1, "select 1 from dual")
	}

	public static void assertEquals(expectedExpression,actualExpression) {
		assert evaluate(expectedExpression)==evaluate(actualExpression)
	}
	
	@Test
	public void testAssertEquals() {
		assertEquals("1+1","2")
	}
	
	public static void assertEquals(expectedExpression,actualExpression,List<Map> records) {
		assert evaluate(expectedExpression)==evaluate(actualExpression,records)
	}
		
	public static String evaluate(expression) {
		getSql().firstRow("select ${expression} from DUAL".toString()).find().value
	}
	
	public static String evaluate(expression,List<Map> records) {
		getSql().firstRow("select ${expression} from (${recordsToSubquery(records)})".toString()).find().value
	}
	
	@Test
	public void testEvaluate() {
		assert "2"==evaluate("1+1")
		assert "11"==evaluate("sum(A)",[["A":6],["A":5]])
	}

	
	private static String recordsToSubquery(List<Map> records) {
		records.collect {mapToQuery(it)}.join("union all\n")
	}
	
	private static String mapToQuery(Map row) {
		String expressions=row.keySet().collect({"${row[it]} as ${it}"}).join(",")
		"select ${expressions} from dual\n"
	}
	
	@Test
	public void testMapToSubQuery() {
		assert '''\
select 1 as a,2 as b from dual
union all
select 2 as a,4 as b from dual
'''==recordsToSubquery([[a:1,b:2],[a:2,b:4]])
	}
	
	
	
	
}
