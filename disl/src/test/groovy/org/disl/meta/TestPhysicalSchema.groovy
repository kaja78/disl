package org.disl.meta

import org.disl.test.DislTestCase;
import org.junit.Assert
import org.junit.Test

class TestPhysicalSchema {
	

	@Test
	void testPassword() {
		Context.setContextName('disl-test')
		PhysicalSchema s1=Context.getContext().getPhysicalSchema("s1")
		PhysicalSchema s2=Context.getContext().getPhysicalSchema("s2")	
		Assert.assertEquals('secure',s1.password)
		Assert.assertEquals('secure',s2.password)
	}

}
