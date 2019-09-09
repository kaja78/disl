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
package org.disl.db.hsqldb.pattern

import org.disl.meta.Column
import org.disl.meta.ColumnMapping
import org.disl.meta.MetaFactory
import org.disl.meta.Table
import org.disl.meta.TableMapping
import org.disl.pattern.Pattern
import org.disl.pattern.TablePattern
import org.disl.pattern.generic.CreateOrReplaceTablePattern;
import org.disl.pattern.generic.TruncateInsertPattern
import org.disl.test.DislTestCase
import org.junit.Test

class TestTruncateInsertMapping extends DislTestCase {
	
	
	static class TestMapping extends TableMapping {
		TruncateInsertPattern pattern
		
		TEST_TABLE target
		TEST_TABLE src
		
		ColumnMapping A=e 1
		ColumnMapping B=e "'2'"
		
		@Override
		public void initMapping() {
			from src
		}
	}
	
	static class TEST_TABLE extends Table {

		CreateOrReplaceTablePattern pattern
		
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
