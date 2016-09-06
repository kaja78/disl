/*
 * Copyright 2015 - 2016 Karel Hübl <karel.huebl@gmail.com>.
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

import groovy.sql.Sql
import groovy.test.GroovyAssert
import groovy.transform.CompileStatic;

import org.disl.meta.Context
import org.disl.meta.PhysicalSchema

/**
 * Abstract parent for Disl test cases. 
 * Disl test cases support unit testing of SQL expression 
 * typically defined within Mappings or shared expression libraries.
 * */
@CompileStatic
abstract class AbstractDislTestCase {

	public Sql getSql() {
		Context.getSql(schema)
	}
	
	public String getSchema() {
		'default'
	}
	
	public PhysicalSchema getPhysicalSchema() {
		Context.getContext().getPhysicalSchema(getSchema())
	}

	public void assertExpressionTrue(expression) {
		assertRowCount(1, physicalSchema.evaluateConditionQuery(expression.toString()))
	}

	public void assertExpressionFalse(expression) {
		assertRowCount(0, physicalSchema.evaluateConditionQuery(expression.toString()))
	}

	public String recordsToSubquery(List<Map> records) {
		physicalSchema.recordsToSubquery(records)
	}

	public void assertRowCount(int expectedCount,String sqlQuery) {
		int actualCount=getRowCount(sqlQuery)
		GroovyAssert.assertEquals("""Invalid rowcount returned from query:
${sqlQuery}
""",expectedCount,actualCount,0)
	}

	public int getRowCount(String sqlQuery) {
		Integer.parseInt(getSql().firstRow("select count(1) from (${sqlQuery})".toString()).getAt(0).toString())		
	}

	public void assertExpressionEquals(expectedExpression,actualExpression) {
		GroovyAssert.assertEquals(evaluate(expectedExpression),evaluate(actualExpression))
	}

	public void assertExpressionEquals(expectedExpression,actualExpression,List<Map> records) {
		GroovyAssert.assertEquals(evaluate(expectedExpression),evaluate(actualExpression,records))
	}

	/**
	 * Evaluate value of SQL expression.
	 * */
	public String evaluate(def expression) {
		return getSql().firstRow(physicalSchema.evaluateExpressionQuery(expression.toString())).getAt(0)
	}
	
	public String evaluate(def expression,List<Map> records) {
		getSql().firstRow("select ${expression} from ${recordsToSubquery(records)}".toString()).getAt(0)
	}
}
