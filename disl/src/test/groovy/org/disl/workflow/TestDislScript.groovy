package org.disl.workflow

import org.disl.meta.MetaFactory;
import org.junit.Test

class TestDislScript {
	
	TestingScript s=MetaFactory.create(TestingScript)
	
	@Test
	void testExecute() {
		s.execute()
	}

}
