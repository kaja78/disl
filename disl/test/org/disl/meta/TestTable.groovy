package org.disl.meta
import org.disl.db.hsqldb.pattern.CreateOrReplaceTablePattern
import org.disl.pattern.Pattern
import org.junit.Test


class TestTable extends Table {

	Pattern pattern=new CreateOrReplaceTablePattern(table: this)
	String schema="L2"
	
	@Description("Column A.")
	@DataType("VARCHAR(255)")
	Column A
	
	@DataType("VARCHAR(255)")
	Column B
	
	@DataType("VARCHAR(255)")
	Column C
	
	
	def DUMMY
	
	
	@Test
	void testGetColumns() {
		assert A==getColumns().get(0)
		assert A.dataType=="VARCHAR(255)"
		assert B==getColumns().get(1)
		assert "Column A."==A.description
		assert A.parent==this
		
	}
	
	

}
