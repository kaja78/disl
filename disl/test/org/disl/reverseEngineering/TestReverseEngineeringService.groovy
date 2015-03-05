package org.disl.reverseEngineering

import org.junit.Test;

class TestReverseEngineeringService {
	@Test
	public void testReverseSchemaTables() {
		def s=new ReverseEngineeringService(logicalSchemaName: "INFORMATION_SCHEMA")
		s.reverseSchemaTables("l2","%TABLE%",new File("build/test"))
		assert new File("build/test/l2/SYSTEM_TABLES.groovy").exists()
	}
}
