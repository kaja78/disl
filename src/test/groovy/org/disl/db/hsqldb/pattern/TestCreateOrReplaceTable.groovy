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
package org.disl.db.hsqldb.pattern

import org.disl.meta.Column
import org.disl.meta.MetaFactory
import org.disl.meta.TestTable.TESTING_TABLE
import org.disl.test.DislTestCase
import org.junit.Test

class TestCreateOrReplaceTable extends DislTestCase {

	TESTING_TABLE t=MetaFactory.create(TESTING_TABLE)

	@Test
	public void testSimulate() {
		t.simulate()
	}

	@Test
	public void testExecute() {
		t.execute()
		t.execute()
	}
}
