package org.disl.pattern

import org.disl.test.DislTestCase
import org.junit.Test

class TestExecuteSqlScriptStep extends DislTestCase {

	@Test
	void testGetCommands() {
		def p=new ExecuteSQLScriptStep(commandSeparator: ExecuteSQLScriptStep.BACKSLASH_NEW_LINE,pattern: "A;B")
		assertEquals(1, p.getCommands().size())
		assertEquals("A;B", p.getCommands()[0])
		p=new ExecuteSQLScriptStep(pattern: "A;B");
		assertEquals(2, p.getCommands().size())
		assertEquals("A", p.getCommands()[0])
		assertEquals("B", p.getCommands()[1])
		
	}
}
