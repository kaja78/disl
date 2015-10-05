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
package org.disl.db.hsqldb.pattern

import org.disl.meta.Column
import org.disl.meta.MetaFactory
import org.disl.meta.Table
import org.disl.pattern.Pattern
import org.disl.pattern.generic.CreateOrReplaceTablePattern;
import org.disl.test.DislTestCase
import org.junit.Test

class TestCreateOrReplaceTable extends DislTestCase {

	static class KEY extends Column {
		String dataType="DECIMAL"
	}

	static class TESTING_TABLE extends Table {
		String  schema="L2"
		Pattern pattern=new CreateOrReplaceTablePattern(table: this)

		KEY TT_KEY
	}
	
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
