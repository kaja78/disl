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
import org.disl.meta.ColumnMapping
import org.disl.meta.MetaFactory
import org.disl.meta.Table
import org.disl.meta.TableMapping
import org.disl.pattern.Pattern
import org.disl.test.DislTestCase
import org.junit.Test

class TestTruncateInsertMapping extends DislTestCase {
	
	
	class TestMapping extends TableMapping {
		Pattern pattern=new TruncateInsertPattern(mapping: this)
		String schema="L2"
		
		TEST_TABLE target
		TEST_TABLE src
		
		ColumnMapping A=e 1
		ColumnMapping B=e "'2'"
		
		@Override
		public void initMapping() {
			from src
		}
	}
	
	class TEST_TABLE extends Table {
		String schema="L2"
		
		Column A
		Column B
	}
	
	@Test
	void testSimulate() {
		def tt=new TEST_TABLE()
		def m=new TestMapping()
		TestMapping t=MetaFactory.create(TestMapping)
		t.simulate()
	}
	
	
}
