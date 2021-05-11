/*
 *
 * Copyright 2015 - 2021 Karel Hübl <karel.huebl@gmail.com>.
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
import groovy.transform.CompileStatic;

import org.disl.meta.Context
import org.disl.meta.PhysicalSchema
import org.junit.Assert

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

	public void assertRowCount(long expectedCount,String sqlQuery) {
		long actualCount=physicalSchema.evaluateRowCount(sqlQuery)
		Assert.assertEquals("""Invalid rowcount returned from query:
${sqlQuery}
""",expectedCount,actualCount)
	}

	public void assertExpressionEquals(expectedExpression,actualExpression) {
		Assert.assertEquals(physicalSchema.evaluateExpression(expectedExpression).toString(),physicalSchema.evaluateExpression(actualExpression).toString())
	}

	public void assertExpressionEquals(expectedExpression,actualExpression,List<Map> records) {
		Assert.assertEquals(physicalSchema.evaluateExpression(expectedExpression).toString(),physicalSchema.evaluateAggregateExpression(actualExpression,records).toString())
	}


}
