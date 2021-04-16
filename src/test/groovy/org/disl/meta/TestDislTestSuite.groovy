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
package org.disl.meta

import static org.junit.Assert.*
import junit.framework.TestSuite

import org.disl.util.test.AbstractDislTestSuite
import org.junit.Ignore
import org.junit.Test

class TestDislTestSuite {
	
	@Ignore
	static class TestingSuite extends AbstractDislTestSuite {
		
	}
	
	TestingSuite ts=new TestingSuite()
	
	@Test
	void testGetTestSuite() {
		TestSuite ts=ts.getTestSuite()
		TestSuite mappings=ts.testAt(0)
		TestSuite tables=ts.testAt(1)
		TestSuite dislTestCases=ts.testAt(2)
			
		assertNotNull mappings.tests().find({it.toString()=='org.disl.meta.TestMapping$TestingMapping'})
		assertNotNull tables.tests().find({it.toString()=='org.disl.meta.TestDimensionTable'})
		assertNotNull dislTestCases.tests().find({it.toString()=='org.disl.meta.TestLibrary'})
	}

}
