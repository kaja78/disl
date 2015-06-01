package org.disl.meta

import static org.junit.Assert.*

import org.disl.test.DislTestCase
import org.junit.Test

class TestContext extends DislTestCase {
	@Test
	void testGetContext() {
		Context context=Context.getContext()
		assert context.name=="disl-test"		
		assert context.getConfig()["L2"]=="Hsqldb"
		assert context.getPhysicalSchema("L2").schema=="PUBLIC"
	}
	
	@Test
	void testGetSql() {
		Context.getSql("L2").execute("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES")
	}

}
