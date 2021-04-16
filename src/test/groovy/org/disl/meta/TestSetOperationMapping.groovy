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
package org.disl.meta

import static org.junit.Assert.*

import org.disl.meta.TestMapping.TestingMapping
import org.disl.test.DislTestCase
import org.junit.Before
import org.junit.Test

class TestSetOperationMapping extends DislTestCase {

	static class TestingSetOperationMapping extends Mapping {
		String schema="L2"

		TestingMapping subquery1
		TestingMapping subquery2

		ColumnMapping A=e "$subquery1.A"
		ColumnMapping c=e "C"
		ColumnMapping B=e "$subquery1.B"

		@Override
		public void initMapping() {
			from subquery1
			union subquery2
		}
	}

	TestingSetOperationMapping mapping=MetaFactory.create(TestingSetOperationMapping)
	
	@Test
	void testGetSQLQuery() {
		assertEquals("""\
	/*Mapping TestingSetOperationMapping*/
		SELECT
			subquery1.A as A,
			C as c,
			subquery1.B as B
		FROM
			(
	/*Mapping TestingMapping*/
		SELECT
			s1.A as A,
			C as c,
			REPEAT(s2.B,3) as B
		FROM
			PUBLIC.TESTING_TABLE s1
			INNER JOIN PUBLIC.TESTING_TABLE s2  ON (s1.A=s2.A)
			LEFT OUTER JOIN PUBLIC.TESTING_TABLE s3  ON (s2.A=s3.A)
			RIGHT OUTER JOIN PUBLIC.TESTING_TABLE s4  ON (s2.A=s4.A)
			FULL OUTER JOIN PUBLIC.TESTING_TABLE s5  ON (s2.A=s5.A)
			CROSS JOIN PUBLIC.TESTING_TABLE s6
		WHERE
			s1.A=s1.A and 1=/*BIND*/p1
		GROUP BY
			s1.A,C,REPEAT(s2.B,3)
	/*End of mapping TestingMapping*/) subquery1
		WHERE
			1=1
		
	UNION select A,c,B from (
	/*Mapping TestingMapping*/
		SELECT
			s1.A as A,
			C as c,
			REPEAT(s2.B,3) as B
		FROM
			PUBLIC.TESTING_TABLE s1
			INNER JOIN PUBLIC.TESTING_TABLE s2  ON (s1.A=s2.A)
			LEFT OUTER JOIN PUBLIC.TESTING_TABLE s3  ON (s2.A=s3.A)
			RIGHT OUTER JOIN PUBLIC.TESTING_TABLE s4  ON (s2.A=s4.A)
			FULL OUTER JOIN PUBLIC.TESTING_TABLE s5  ON (s2.A=s5.A)
			CROSS JOIN PUBLIC.TESTING_TABLE s6
		WHERE
			s1.A=s1.A and 1=/*BIND*/p1
		GROUP BY
			s1.A,C,REPEAT(s2.B,3)
	/*End of mapping TestingMapping*/) subquery2
	/*End of mapping TestingSetOperationMapping*/""", mapping.getSQLQuery())
	}

	@Test
	void testGetSetOperationClause() {
		assertEquals("""\n\tUNION select A,c,B from (\n	/*Mapping TestingMapping*/
		SELECT
			s1.A as A,
			C as c,
			REPEAT(s2.B,3) as B
		FROM
			PUBLIC.TESTING_TABLE s1
			INNER JOIN PUBLIC.TESTING_TABLE s2  ON (s1.A=s2.A)
			LEFT OUTER JOIN PUBLIC.TESTING_TABLE s3  ON (s2.A=s3.A)
			RIGHT OUTER JOIN PUBLIC.TESTING_TABLE s4  ON (s2.A=s4.A)
			FULL OUTER JOIN PUBLIC.TESTING_TABLE s5  ON (s2.A=s5.A)
			CROSS JOIN PUBLIC.TESTING_TABLE s6
		WHERE
			s1.A=s1.A and 1=/*BIND*/p1
		GROUP BY
			s1.A,C,REPEAT(s2.B,3)
	/*End of mapping TestingMapping*/) subquery2""",mapping.getSetOperationClause())
	}
}
