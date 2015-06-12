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
package org.disl.meta

import org.disl.meta.TestMapping.TestingMapping
import org.disl.test.DislTestCase
import org.junit.Before
import org.junit.Test

class TestSubMapping extends DislTestCase {

	class TestingSubMapping extends Mapping {
		String schema="L2"

		ColumnMapping A=e {"${subquery.A}"}
		ColumnMapping B=e "1"

		TestingMapping subquery

		@Override
		public void initMapping() {
			from subquery
		}
	}

	@Before
	void createTestTable() {
		MetaFactory.create(TestTable).execute()
	}

	@Test
	void testGetSQLQuery() {
		TestingSubMapping m=MetaFactory.create(TestingSubMapping)
		println m.getSQLQuery()
	}
}
