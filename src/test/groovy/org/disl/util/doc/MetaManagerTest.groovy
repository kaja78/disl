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
package org.disl.util.doc;

import static org.junit.Assert.*;

import org.junit.Test;

public class MetaManagerTest {
	MetaManager mm=new MetaManager()

	@Test
	public void testAddRootPackage() {
		mm.addRootPackage('org.disl')
		mm.process({})
		assertTrue(mm.packageContent.get('org.disl.meta').contains('org.disl.meta.TestSubMapping$TestingSubMapping'))
		assertTrue(mm.sourceUsage.get('org.disl.meta.TestMapping$TestingMapping').contains('org.disl.meta.TestSubMapping$TestingSubMapping'))
		assertTrue(mm.targetUsage.get('org.disl.db.hsqldb.pattern.TestTruncateInsertMapping$TEST_TABLE').contains('org.disl.db.hsqldb.pattern.TestTruncateInsertMapping$TestMapping'))
	}

}
