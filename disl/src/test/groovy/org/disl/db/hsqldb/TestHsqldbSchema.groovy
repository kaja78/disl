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
package org.disl.db.hsqldb

import org.disl.meta.Context
import org.disl.test.DislTestCase
import org.junit.Test
import static org.junit.Assert.*

class TestHsqldbSchema extends DislTestCase {

	HsqldbSchema physicalSchema=Context.getContext().getPhysicalSchema('default')
	
	@Test
	void testSql() {
		Context.setContextName('disl-test')
		physicalSchema.getSql().execute("SELECT 1 FROM (VALUES (0))")
	}
	
	@Test
	public void testEvaluate() {
		assertEquals(2,physicalSchema.evaluateExpression("1+1"))
		assertEquals(11,physicalSchema.evaluateAggregateExpression("sum(A)",[["A":6], ["A":5]]))
		assertEquals(11,physicalSchema.evaluateAggregateExpression("sum(A)",[["A":6,"B.B":1], ["A":5,"B.B":1]]))
		assertEquals(2,physicalSchema.evaluateAggregateExpression("sum(B.B)",[["A.A":6,"B.B":1], ["A.A":5,"B.B":1]]))
	}
	
	@Test
	public void testMapToSubQuery() {
		assertEquals '''\
(select 1 as DUMMY_KEY,1 as a,2 as b from (VALUES(0))
\t\tunion all
\t\tselect 2 as DUMMY_KEY,2 as a,4 as b from (VALUES(0))) SRC
\twhere 1=1 AND SRC.DUMMY_KEY=SRC.DUMMY_KEY''',physicalSchema.recordsToSubquery([["a":1,"b":2], ["a":2,"b":4]])
		assertEquals '''\
(select 1 as DUMMY_KEY,1 as a from (VALUES(0))
\t\tunion all
\t\tselect 2 as DUMMY_KEY,2 as a from (VALUES(0))) A,
\t\t(select 1 as DUMMY_KEY,2 as b from (VALUES(0))
\t\tunion all
\t\tselect 2 as DUMMY_KEY,4 as b from (VALUES(0))) B
\twhere 1=1 AND A.DUMMY_KEY=A.DUMMY_KEY
\t\tAND B.DUMMY_KEY=A.DUMMY_KEY''',physicalSchema.recordsToSubquery([["A.a":1,"B.b":2], ["A.a":2,"B.b":4]])
	}

}
