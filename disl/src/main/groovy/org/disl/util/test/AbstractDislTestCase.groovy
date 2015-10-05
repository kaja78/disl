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
package org.disl.util.test

import java.util.List;
import java.util.Map;

import groovy.sql.Sql
import groovy.test.GroovyAssert

import org.disl.db.oracle.OracleLookup

/**
 * Abstract parent for Disl test cases. 
 * Disl test cases support unit testing of SQL expression 
 * typically defined within Mappings or shared expression libraries.
 * */
abstract class AbstractDislTestCase {
	static Sql sql

	protected Sql getSql() {
		if (sql==null) {
			sql=createSql()
		}
		sql
	}


	/**
	 * Factory method for creating database connection. The connection will be used to evaluate SQL expression in assertExpression* methods.
	 * */
	protected abstract Sql createSql();

	/**
	 * Database dialect specific implementation of evaluating SQL Expression. @see org.disl.db.oracle.OracleDislTestCase.
	 * */
	public abstract String evaluate(expression);

	/**
	 * Database dialect specific implementation of evaluating boolean SQL Expression. @see org.disl.db.oracle.OracleDislTestCase.
	 * */
	public abstract void assertExpressionTrue(expression);

	/**
	 * Database dialect specific implementation of evaluating boolean SQL Expression. @see org.disl.db.oracle.OracleDislTestCase.
	 * */
	public abstract void assertExpressionFalse(expression);

	/**
	 * Database dialect specific implementation of generating SQL query returning resultset with passed records. @see org.disl.db.oracle.OracleDislTestCase.
	 * */
	public abstract String recordsToSubquery(List<Map> records);

	public void assertRowCount(int expectedCount,String sqlQuery) {
		int actualCount=getRowCount(sqlQuery)
		GroovyAssert.assertEquals("""Invalid rowcount returned from query:
${sqlQuery}
""",expectedCount,actualCount,0)
	}

	public int getRowCount(String sqlQuery) {
		getSql().firstRow("select count(1) from (${sqlQuery})".toString()).find().value
	}

	public void assertExpressionEquals(expectedExpression,actualExpression) {
		GroovyAssert.assertEquals(evaluate(expectedExpression),evaluate(actualExpression))
	}

	public void assertExpressionEquals(expectedExpression,actualExpression,List<Map> records) {
		GroovyAssert.assertEquals(evaluate(expectedExpression),evaluate(actualExpression,records))
	}

	public String evaluate(expression,List<Map> records) {
		getSql().firstRow("select ${expression} from ${recordsToSubquery(records)}".toString()).find().value
	}
}
