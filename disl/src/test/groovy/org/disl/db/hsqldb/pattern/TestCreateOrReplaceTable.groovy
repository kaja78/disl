package org.disl.db.hsqldb.pattern

import org.disl.meta.Column
import org.disl.meta.Context
import org.disl.meta.MetaFactory
import org.disl.meta.Table
import org.disl.pattern.Pattern
import org.junit.Before
import org.junit.Test

class TestCreateOrReplaceTable {

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
