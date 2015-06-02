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
