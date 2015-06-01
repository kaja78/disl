package org.disl.db.hsqldb

import org.disl.test.DislTestCase
import org.junit.Test

class TestHsqldbSchema extends DislTestCase {

	@Test
	void testSql() {
		new HsqldbSchema().getSql().execute("CREATE TABLE A (A CHAR(1))")
	}
}
