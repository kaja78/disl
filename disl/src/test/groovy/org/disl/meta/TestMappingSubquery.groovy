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

import org.disl.test.DislTestCase
import org.junit.Before
import org.junit.Test

class TestMappingSubquery extends DislTestCase {

	TestingMapping t

	TestMappingSubquery() {
		t=MetaFactory.create(TestingMapping)
		t.t.execute()
	}

	@Test
	void testGetSQLQuery() {
		assertEquals ("""	/*Mapping TestingMapping*/
		SELECT
			t.A as A
		FROM
			PUBLIC.TestTable t
		WHERE
			t.A in (
	/*Mapping Subquery*/
		SELECT
			t.B as B
		FROM
			PUBLIC.TestTable t
		WHERE
			1=1
		
	/*End of mapping Subquery*/
)
		
	/*End of mapping TestingMapping*/""",t.getSQLQuery())
	}

	@Test
	void testValidate() {
		t.validate()
	}

	static class TestingMapping extends Mapping {

		String schema="L2"

		TestTable t
		Subquery s

		ColumnMapping A=e {t.A}

		void initMapping() {
			from t
			where """${t.A} in (
${s.SQLQuery}
)"""
		}

		static class Subquery extends Mapping {

			String schema="L2"

			TestTable t

			ColumnMapping B=e {t.B}

			void initMapping() {
				from t
			}
		}
	}
}
