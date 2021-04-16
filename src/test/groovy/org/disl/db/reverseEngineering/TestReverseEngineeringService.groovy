/*
 *
 * Copyright 2015 - 2021 Karel Hübl <karel.huebl@gmail.com>.
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
package org.disl.db.reverseEngineering

import org.disl.meta.Context;
import org.disl.test.DislTestCase;
import org.junit.Test;
import static groovy.test.GroovyAssert.*

class TestReverseEngineeringService extends DislTestCase {
	
	ReverseEngineeringService s=new ReverseEngineeringService(logicalSchemaName: "default")
	
	@Test
	public void testReverseSchemaTables() {		
		s.reverseSchemaTables("l2","%DUAL%",null,new File("build/test"))
		assert new File("build/test/l2/DUAL.groovy").exists()
	}
	
	@Test
	public void testTraceColumnMappings() {
		s.traceColumnMappings('select 1 one, 2 two from dual')
	}
	
	@Test
	public void testGetAbstractParentTableFileName() {
		assertEquals("AbstractL0Table", s.getAbstractParentTableClassSimpleName("org.disl.l0"))
	}
	
	@Test
	public void testGetAbstractParentTableSourceCode() {
		String packageName="org.disl.l0"
		String expectedCode="""\
package org.disl.l0

import org.disl.meta.Table

public abstract class AbstractL0Table  extends Table {
		String schema="L0"
}
"""
		s.logicalSchemaName="L0"
		assertEquals(expectedCode, s.getAbstractParentTableSourceCode('org.disl.l0'))
	}
}
