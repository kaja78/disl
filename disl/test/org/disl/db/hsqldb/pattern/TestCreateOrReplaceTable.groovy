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
		String  schema=Contexts.L0
		Pattern pattern=new CreateOrReplaceTable(table: this)

		KEY TT_KEY
	}
	
	TESTING_TABLE t=MetaFactory.create(TESTING_TABLE)

	@Before
	public void init() {
		Context.set(Contexts.DEV)
	}


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
