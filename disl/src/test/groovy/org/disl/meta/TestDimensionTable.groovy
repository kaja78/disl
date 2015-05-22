package org.disl.meta


@UniqueKey(columns=["ID","NAME"])
@Description("This is testing dimension.")
class TestDimensionTable extends Table {
	String schema="L2"
	
	@PrimaryKey
	@DataType("INTEGER")
	@Description("Surrogate key.")
	Column KEY
	
	@UniqueKey
	@DataType("INTEGER")
	@Description("Natural key")
	Column ID
	
	@DataType("VARCHAR(200)")
	@Description("Dimension name.")
	Column NAME
	
	
	
}
