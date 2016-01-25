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
package org.disl.meta

import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*

class TestLookup {
	
	TestingLookup l
	TestingLookupMapping m
	
	@Before
	public void initTest() {
		Context.setContextName('disl-test')
		l=MetaFactory.create(TestingLookup)
		m=MetaFactory.create(TestingLookupMapping)
	}
	
	@Test
	public void testMapToQuery() {
		assertEquals("select 1 as DUMMY_KEY,1 as A,2 as B from (VALUES(0))\n",l.getPhysicalSchema().mapToQuery(["A.A":1,"B":2], "A",1, true))
		assertEquals("select 2 as DUMMY_KEY,1 as A from (VALUES(0))\n",l.getPhysicalSchema().mapToQuery(["A.A":1,"B":2], "A", 2,false))
	}
	
	@Test
	public void testGetQuery() {
		println m.getSQLQuery()
	}
	
	@Test
	public void testGetRefference() {
		assertEquals("""(select * from (select 1 as DUMMY_KEY,1 as A,2 as B from (VALUES(0))
union all
select 2 as DUMMY_KEY,2 as A,4 as B from (VALUES(0))
) SRC
where
1=1
AND SRC.DUMMY_KEY=SRC.DUMMY_KEY)""",l.getRefference())
	}
	
	static class TestingLookup extends Lookup {
		
		Column A
		Column B
		
		List<List> records=[[1,2], [2,4]]
		
		
	}
	
	static class TestingLookupMapping extends Mapping {
		
		TestingLookup L
		
		ColumnMapping C= e L.A+L.B
		
		@Override
		public void initMapping() {
			from L
		}
		
	}
}
