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

		ColumnMapping A=e {"$subquery1.A"}
		ColumnMapping c=e "C"
		ColumnMapping B=e {"$subquery1.B"}

		@Override
		public void initMapping() {
			from subquery1
			union subquery2
		}
	}

	TestingSetOperationMapping mapping=MetaFactory.create(TestingSetOperationMapping)
	
	@Before
	void createTestTable() {
		MetaFactory.create(TestTable).execute()
	}

	@Test
	void testGetSQLQuery() {
		println mapping.getSQLQuery()
	}

	@Test
	void testGetSetOperationClause() {
		assertEquals("""\n\tUNION 	/*Mapping TestingMapping*/
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
	/*End of mapping TestingMapping*/""",mapping.getSetOperationClause())
	}
}
