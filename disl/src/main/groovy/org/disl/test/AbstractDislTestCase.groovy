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
 * along with Disl.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.disl.test

import static org.junit.Assert.*

import java.util.List;
import java.util.Map;

import org.disl.db.oracle.OracleLookup;

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
	
	public void assertExpressionFalse(expression) {
		assertRowCount(0, "select 1 from dual where ${expression}")
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
	
	public String recordsToSubquery(List<Map> records) {
		return OracleLookup.recordsToSubquery(records)
	}
}
