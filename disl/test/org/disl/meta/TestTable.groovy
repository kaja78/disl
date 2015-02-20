package org.disl.meta
import org.junit.Test


class TestTable extends Table {

	@Description("Column A.")
	Column A
	
	Column B
	
	
	def DUMMY
	
	
	@Test
	void testGetColumns() {
		init()
		assert A==getColumns().get(0)
		assert B==getColumns().get(1)
		assert "Column A."==A.description
		assert A.parent==this
		
	}
	
	

}
