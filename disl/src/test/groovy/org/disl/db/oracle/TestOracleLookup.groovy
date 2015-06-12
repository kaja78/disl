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
package org.disl.db.oracle

import org.junit.Test

class TestOracleLookup extends GroovyTestCase {
	OracleLookup l=new OracleLookup() {
		List<Map> records=[[a:1,b:2], [a:2,b:4]]
	}

	@Test
	public void testMapToQuery() {
		assert "select 1 as DUMMY_KEY,1 as A,2 as B from dual\n"==OracleLookup.mapToQuery(["A.A":1,"B":2], "A",1, true)
		assert "select 2 as DUMMY_KEY,1 as A from dual\n"==OracleLookup.mapToQuery(["A.A":1,"B":2], "A", 2,false)
	}
	
	@Test
	public void testGetRefference() {
		assertEquals("""(select * from (select 1 as DUMMY_KEY,1 as a,2 as b from dual
union all
select 2 as DUMMY_KEY,2 as a,4 as b from dual
) SRC
where
1=1
AND SRC.DUMMY_KEY=SRC.DUMMY_KEY)""",l.getRefference())
	}
}
