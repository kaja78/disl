package org.disl.meta

import org.junit.Before;
import org.junit.Test;

class TestSubMapping extends Mapping {
	String schema="L2"
	
	ColumnMapping A=e {"${subquery.A}"}
	ColumnMapping B=e "1"
	
	TestMapping subquery

	@Override
	public void initMapping() {
		from subquery		
	}
	
	@Before
	void createTestTable() {
		MetaFactory.create(TestTable).execute()
	}
	
	@Test
	void testGetSQLQuery() {
		TestSubMapping m=MetaFactory.create(TestSubMapping)
		println m.getSQLQuery()
	}

}
