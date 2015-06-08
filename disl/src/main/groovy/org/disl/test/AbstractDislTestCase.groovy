/*
 * Copyright 2015 Karel Hübl <karel.huebl@gmail.com>.
 *
 * This file is part of disl.
 *
 * Disl is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Disl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.disl.test

import static org.junit.Assert.*
import groovy.sql.Sql

abstract class AbstractDislTestCase extends GroovyTestCase {

	static Sql sql

	protected Sql getSql() {
		if (sql==null) {
			sql=createSql()
		}
		sql
	}
	
	protected abstract Sql createSql();

	public void assertExpressionTrue(expression) {
		assertRowCount(1, "select 1 from dual where ${expression}")
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

	public void assertExpressionEquals(expectedExpression,actualExpression) {
		assertEquals(evaluate(expectedExpression),evaluate(actualExpression))
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

	protected String recordsToSubquery(List<Map> records) {
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

}
