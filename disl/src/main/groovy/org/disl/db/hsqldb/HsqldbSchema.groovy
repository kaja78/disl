package org.disl.db.hsqldb

import static org.junit.Assert.*

import org.disl.meta.PhysicalSchema
import org.junit.Test

class HsqldbSchema extends PhysicalSchema {
	HsqldbSchema() {
		jdbcDriver="org.hsqldb.jdbcDriver"
		jdbcUrl="jdbc:hsqldb:mem:inmemoryDb"
		user="sa"
	}

	@Test
	void testSql() {
		sql.execute("CREATE TABLE A (A CHAR(1))")
	}
}
