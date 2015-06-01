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
