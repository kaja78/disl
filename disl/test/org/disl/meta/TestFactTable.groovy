package org.disl.meta;


@Description("Testing fact table.")
public class TestFactTable {
	
	@PrimaryKey
	Column ID
	
	@ForeignKey(targetTable=TestDimensionTable,targetColumn="KEY")	
	Column DIMENSION_KEY
	
	@Description("Amount measure.")
	Column AMOUNT;

}
