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
