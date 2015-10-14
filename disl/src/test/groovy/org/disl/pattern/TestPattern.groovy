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
import org.disl.meta.Context
import org.junit.Before
import org.junit.Test

class TestPattern  {
	
	@Before
	void initTest() {
		Context.setContextName('disl-test')
	}

	@Test
	public void testSimulate() {
		Context ctx=Context.getContext()
		TestingPattern testingPattern=new TestingPattern(element:new BaseMock());
		testingPattern.init()
		testingPattern.simulate();
		assertEquals("DROP \nBaseMock", testingPattern.steps[0].code)
	}

	private class BaseMock extends Base {}

	static class TestingPattern extends Pattern {
		Base element;
		@Override
		public void init() {
			add TestingStep
		}
	}

	static class TestingStep extends Step {
		String getCode() {
			"""\
DROP 
${getPattern().element.name}"""
		}
		
		@Override
		protected int executeInternal() {
			return 0;
		}
	}
}
