package org.disl.meta


@Description("This is testing dimension.")
class TestDimensionTable extends Table {
	
	@PrimaryKey
	@Description("Surrogate key.")
	Column KEY
	
	@Description("Natural key")
	Column ID
	
	@Description("Dimension name.")
	Column NAME
	
}
