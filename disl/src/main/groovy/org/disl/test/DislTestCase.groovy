package org.disl.test

import static org.junit.Assert.*

import groovy.sql.Sql
import org.disl.db.hsqldb.HsqldbSchema
import org.junit.Before
import org.junit.Test

class DislTestCase extends GroovyTestCase {

	static Sql sql

	protected Sql getSql() {
		if (sql==null) {
			sql=createSql()
		}
		sql
	}
	
	//TODO: Make abstract.
	protected Sql createSql() {
		def sql=new HsqldbSchema().getSql()
		sql.execute("CREATE TABLE DUAL (dummy char(1))")
		sql.execute("INSERT INTO DUAL VALUES ('X')")
		return sql
		
	}

	public void assertExpressionTrue(expression) {
		assertRowCount(1, "select 1 from dual where ${expression}")
	}

	@Test
	public void testAssertExpressionTrue() {
		assertExpressionTrue("1=1")
	}

	public void assertRowCount(int expectedCount,String sqlQuery) {
		int actualCount=getRowCount(sqlQuery)
		assertEquals("""Invalid rowcount returned from query:
${sqlQuery}
""",expectedCount,actualCount,0)
	}

	public int getRowCount(String sqlQuery) {
		getSql().firstRow("select count(1) from (${sqlQuery})".toString()).find().value
	}

	@Test
	public void testAssertRowCount() {
		assertRowCount(1, "select 1 from dual")
	}

	public void assertExpressionEquals(expectedExpression,actualExpression) {
		assertEquals(evaluate(expectedExpression),evaluate(actualExpression))
	}

	@Test
	public void testAssertExpressionEquals() {
		assertExpressionEquals("1+1","2")
	}

	public void assertExpressionEquals(expectedExpression,actualExpression,List<Map> records) {
		assertEquals(evaluate(expectedExpression),evaluate(actualExpression,records))
	}

	public String evaluate(expression) {
		getSql().firstRow("select ${expression} from DUAL".toString()).find().value
	}

	public String evaluate(expression,List<Map> records) {
		getSql().firstRow("select ${expression} from ${recordsToSubquery(records)}".toString()).find().value
	}

	@Test
	public void testEvaluate() {
		assert "2"==evaluate("1+1")
		assert "11"==evaluate("sum(A)",[["A":6], ["A":5]])
		assert "11"==evaluate("sum(A)",[["A":6,"B.B":1], ["A":5,"B.B":1]])
		assert "2"==evaluate("sum(B.B)",[["A.A":6,"B.B":1], ["A.A":5,"B.B":1]])
	}


	private String recordsToSubquery(List<Map> records) {
		String joinCondition=""		
		List aliases=findAliases(records)
		boolean firstSource=true
		String sourceList=aliases.collect {
			String alias=it
			int index=0
			String innerQuery=records.collect {index++; mapToQuery(it,alias,index,firstSource)}.join("union all\n")
			firstSource=false
			return "(${innerQuery}) $alias"
		}.join(",\n")
		joinCondition=aliases.collect({"AND ${it}.DUMMY_KEY=${aliases[0]}.DUMMY_KEY"}).join("\n")
		return """${sourceList}
where
1=1
${joinCondition}"""
	}

	private List findAliases(List<Map> records) {
		List aliases=[]
		records[0].keySet().each {
			String columnName=it.toString()
			if (columnName.contains('.')) {
				aliases.add(columnName.substring(0, columnName.indexOf('.')))
			}
		}
		if (aliases.size()==0) {
			aliases.add("SRC")
		}
		return aliases
	}

	protected String mapToQuery(Map row, String sourceAlias, int index,boolean includeMissingSourceAliasColumns) {
		Map sourceAliasRow=row.findAll {
			String key=it.key.toString()
			return key.startsWith("${sourceAlias}.") || (includeMissingSourceAliasColumns && !key.contains('.'))
		}
		sourceAliasRow=sourceAliasRow.collectEntries {key, value ->
			if (key.startsWith("${sourceAlias}.")) {
				key=key.substring(key.indexOf('.')+1)
			}
			[key, value]
		}
		String expressions=sourceAliasRow.collect({key, value -> "${value} as ${key}" }).join(",")
		return "select ${index} as DUMMY_KEY,${expressions} from dual\n"
	}

	@Test
	public void testMapToQuery() {
		assert "select 1 as DUMMY_KEY,1 as A,2 as B from dual\n"==mapToQuery(["A.A":1,"B":2], "A",1, true)
		assert "select 2 as DUMMY_KEY,1 as A from dual\n"==mapToQuery(["A.A":1,"B":2], "A", 2,false)
	}


	@Test
	public void testMapToSubQuery() {
		assertEquals '''\
(select 1 as DUMMY_KEY,1 as a,2 as b from dual
union all
select 2 as DUMMY_KEY,2 as a,4 as b from dual
) SRC
where
1=1
AND SRC.DUMMY_KEY=SRC.DUMMY_KEY''',recordsToSubquery([["a":1,"b":2], ["a":2,"b":4]])
		assertEquals '''\
(select 1 as DUMMY_KEY,1 as a from dual
union all
select 2 as DUMMY_KEY,2 as a from dual
) A,
(select 1 as DUMMY_KEY,2 as b from dual
union all
select 2 as DUMMY_KEY,4 as b from dual
) B
where
1=1
AND A.DUMMY_KEY=A.DUMMY_KEY
AND B.DUMMY_KEY=A.DUMMY_KEY''',recordsToSubquery([["A.a":1,"B.b":2], ["A.a":2,"B.b":4]])
	}
}
