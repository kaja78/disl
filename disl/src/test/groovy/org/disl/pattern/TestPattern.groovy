/*
 * Copyright 2015 Karel Hübl <karel.huebl@gmail.com>.
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

import org.disl.meta.Base
import org.junit.Test

class TestPattern  {
	
	@Test
	public void testSimulate() {
		Pattern testingPattern=new TestingPattern(element:new Base());		
		testingPattern.simulate();
		assertEquals("DROP \nBase", testingPattern.steps[0].code)
		
		
	}
	
	class TestingPattern extends Pattern {
		Base element;
		
		@Override
		public List<Step> createSteps() {
			[new ExecuteSQLScriptStep(name:"1", pattern: """\
DROP 
${element.name}""")]
		}
		
	
		
		
	}
	
	
	
}
