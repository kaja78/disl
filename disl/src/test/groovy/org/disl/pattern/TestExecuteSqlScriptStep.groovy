/*
 * Copyright 2015 - 2016 Karel Hübl <karel.huebl@gmail.com>.
 *
 * This file is part of disl.
 *
 * Disl is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Disl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Disl.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.disl.pattern

import static groovy.test.GroovyAssert.*
import groovy.sql.Sql

import org.disl.test.DislTestCase
import org.junit.Test

class TestExecuteSqlScriptStep extends DislTestCase {

	@Test
	void testGetCommands() {
		def p=new TestingExecuteSQLScriptStep(commandSeparator: ExecuteSQLScriptStep.BACKSLASH_NEW_LINE,code: "A;B")
		assertEquals(1, p.getCommands().size())
		assertEquals("A;B", p.getCommands()[0])
		p=new TestingExecuteSQLScriptStep(code: "A;B");
		assertEquals(2, p.getCommands().size())
		assertEquals("A", p.getCommands()[0])
		assertEquals("B", p.getCommands()[1])
		p=new TestingExecuteSQLScriptStep(commandSeparator: ExecuteSQLScriptStep.BACKSLASH_NEW_LINE ,code: """\
/*1st block*/
begin
	null;
end;
\\
/*2nd block*/
begin
	null;
end;
""")
		def commands=p.getCommands()
		assertEquals(2, p.getCommands().size())
		assertEquals("""\
/*1st block*/
begin
	null;
end;
""", p.getCommands()[0])
		assertEquals("""\
/*2nd block*/
begin
	null;
end;
""", p.getCommands()[1])
			
		
	}
	
	static class TestingExecuteSQLScriptStep extends ExecuteSQLScriptStep {
		String code
		Sql sql		
	}
}
