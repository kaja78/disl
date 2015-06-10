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
package org.disl.meta

import static org.disl.meta.TestLibrary.*
import static org.junit.Assert.*

import org.disl.test.DislTestCase
import org.junit.Before
import org.junit.Test


class TestMapping extends DislTestCase {

	static class TestingMapping extends Mapping {
		String schema="L2"

		TestTable s1
		TestTable s2
		TestTable s3
		TestTable s4
		TestTable s5
		TestTable s6

		ColumnMapping A=e {"$s1.A"}
		ColumnMapping c=e "C"
		ColumnMapping B=a {repeat(s2.B,3)}

		SqlExpression CONSTANT=constant 1

		void initMapping() {
			from s1
			innerJoin s2 on "$s1.A=$s2.A"
			leftOuterJoin s3 on "$s2.A=$s3.A"
			rightOuterJoin s4 on "$s2.A=$s4.A"
			fullOuterJoin s5 on "$s2.A=$s5.A"
			cartesianJoin s6

			where "$s1.A=$s1.A"

			groupBy "$s1.A,C,${repeat(s2.B,3)}"
		}
		
		@Before
		void createTestTable() {
			MetaFactory.create(TestTable).execute()
		}
	}

	

	@Test
	void testGetSQLQuery() {
		TestingMapping mapping=MetaFactory.create(TestingMapping)
		assertEquals ("""	/*Mapping TestingMapping*/
		SELECT
			s1.A as A,
			C as c,
			REPEAT(s2.B,3) as B
		FROM
			PUBLIC.TestTable s1
			INNER JOIN PUBLIC.TestTable s2  ON (s1.A=s2.A)
			LEFT OUTER JOIN PUBLIC.TestTable s3  ON (s2.A=s3.A)
			RIGHT OUTER JOIN PUBLIC.TestTable s4  ON (s2.A=s4.A)
			FULL OUTER JOIN PUBLIC.TestTable s5  ON (s2.A=s5.A)
			,PUBLIC.TestTable s6
		WHERE
			s1.A=s1.A
		GROUP BY
			s1.A,C,REPEAT(s2.B,3)
	/*End of mapping TestingMapping*/""".toString(),mapping.getSQLQuery())
	}
	
	@Test
	void testConstant() {
		TestingMapping mapping=MetaFactory.create(TestingMapping)
		assertEquals("1",(String)mapping.CONSTANT)
		assertEquals("1+1",(String)mapping.CONSTANT+1)
	}
}
