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

import org.junit.Test
import static groovy.test.GroovyAssert.*

class TestDislTestCase extends DislTestCase {
	
	@Test
	public void testAssertExpressionTrue() {
		assertExpressionTrue("1=1")
	}
	
	@Test
	public void testAssertRowCount() {
		assertRowCount(1, "select 1 from dual")
	}
	
	@Test
	public void testAssertExpressionEquals() {
		assertExpressionEquals("1+1","2")
	}
	
	@Test
	public void testEvaluate() {
		assert "2"==evaluate("1+1")
		assert "11"==evaluate("sum(A)",[["A":6], ["A":5]])
		assert "11"==evaluate("sum(A)",[["A":6,"B.B":1], ["A":5,"B.B":1]])
		assert "2"==evaluate("sum(B.B)",[["A.A":6,"B.B":1], ["A.A":5,"B.B":1]])
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
