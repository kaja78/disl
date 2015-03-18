package org.disl.meta

import org.junit.Test;

class TestSubMapping extends Mapping {
	
	ColumnMapping A=e "1"
	
	TestMapping subquery

	@Override
	public void initMapping() {
		from subquery		
	}
	
	@Test
	void testGetSQLQuery() {
		TestSubMapping m=MetaFactory.create(TestSubMapping)
		println m.getSQLQuery()
	}

}
