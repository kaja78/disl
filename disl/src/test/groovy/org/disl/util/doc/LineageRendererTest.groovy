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
package org.disl.util.doc

import org.disl.db.hsqldb.pattern.TestTruncateInsertMapping.TEST_TABLE
import org.disl.meta.TestMapping.TestingMapping
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LineageRendererTest {
	LineageRenderer renderer=new LineageRenderer()
	
	@Before
	void init() {
		renderer.sources=[new TEST_TABLE(),new TEST_TABLE(),new TEST_TABLE(),new TEST_TABLE(),new TEST_TABLE()]
		renderer.element=new TestingMapping()
		renderer.targets=[new TEST_TABLE(),new TEST_TABLE()]
	}
	
	
	@Test
	void testToHtml() {
		String expected="""\
<a href='org.disl.db.hsqldb.pattern.TestTruncateInsertMapping\$TEST_TABLE.html'>TEST_TABLE</a> -->                              
<a href='org.disl.db.hsqldb.pattern.TestTruncateInsertMapping\$TEST_TABLE.html'>TEST_TABLE</a> -->                --> <a href='org.disl.db.hsqldb.pattern.TestTruncateInsertMapping\$TEST_TABLE.html'>TEST_TABLE</a>
<a href='org.disl.db.hsqldb.pattern.TestTruncateInsertMapping\$TEST_TABLE.html'>TEST_TABLE</a> --> TestingMapping --> <a href='org.disl.db.hsqldb.pattern.TestTruncateInsertMapping\$TEST_TABLE.html'>TEST_TABLE</a>
<a href='org.disl.db.hsqldb.pattern.TestTruncateInsertMapping\$TEST_TABLE.html'>TEST_TABLE</a> -->                              
<a href='org.disl.db.hsqldb.pattern.TestTruncateInsertMapping\$TEST_TABLE.html'>TEST_TABLE</a> -->                              
"""
		Assert.assertEquals(expected,renderer.toHtml())
	}

}
