/*
 * Copyright 2015 - 2016 Karel Hübl <karel.huebl@gmail.com>.
 *
 * This file is part of disl.
 *
 * Disl is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Disl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Disl.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.disl.meta
import static org.junit.Assert.*

import org.disl.meta.Table.ForeignKeyMeta
import org.disl.pattern.TablePattern
import org.disl.pattern.generic.CreateOrReplaceTablePattern
import org.junit.Before
import org.junit.Test

@Indexes(@Index(columns=["A","B"]))
@ForeignKeys([
	@ForeignKey(name='PARENT1_FK',targetTable=TestTable,sourceColumn='PARENT1_B,PARENT1_C'),
	@ForeignKey(name='PARENT2_FK',targetTable=TestTable,sourceColumn='PARENT2_B,PARENT2_C',targetColumn=('B,C'))])
class TestTable extends Table {

	CreateOrReplaceTablePattern pattern
	
	@Description("Column A.")
	@DataType("VARCHAR(255)")
	Column A
	
	@PrimaryKey
	@DataType("VARCHAR(255)")
	Column B
	
	@PrimaryKey
	@DataType("VARCHAR(255)")
	Column C
	
	@DataType("VARCHAR(255)")
	Column PARENT1_B
	
	@DataType("VARCHAR(255)")
	Column PARENT1_C
	
	@DataType("VARCHAR(255)")
	Column PARENT2_B
	
	@DataType("VARCHAR(255)")
	Column PARENT2_C
	
	@DataType("VARCHAR(255)")
	@ForeignKey(targetTable=TestTable,targetColumn='A')
	Column PARENT3_A

	def DUMMY
	
	@Before
	void initTest() {
		init()
	}
	
	@Test
	void testGetColumns() {
		assert A==getColumns().get(0)
		assert A.dataType=="VARCHAR(255)"
		assert B==getColumns().get(1)
		assert "Column A."==A.description
		assert A.parent==this
		
	}
	
	@Test
	void testGetIndexes() {
		Collection indexes=getIndexes()
		assert indexes!=null
		assert indexes[0].columnNames[0]=="A"
		assert indexes[0].columnNames[1]=="B"
	}
	
	@Test
	void testGetPrimaryKeyColumns() {
		assertEquals([B,C], getPrimaryKeyColumns())
	}
	
	@Test
	void testGetForeignKeys() {
	 	ForeignKeyMeta f

		f=getForeignKeys()[0]
		assertEquals('',f.getName())
		assertEquals('TestTable',f.getTargetTable().getName())
		assertEquals('A',f.getTargetColumn())
		assertEquals('PARENT3_A',f.getSourceColumn())
 
		f=getForeignKeys()[1]
		assertEquals('PARENT1_FK',f.getName())
		assertEquals('TestTable',f.getTargetTable().getName())
		assertEquals('B,C',f.getTargetColumn())
		assertEquals('PARENT1_B,PARENT1_C',f.getSourceColumn())
 
		f=getForeignKeys()[2]
		assertEquals('PARENT2_FK',f.getName())
		assertEquals('TestTable',f.getTargetTable().getName())
		assertEquals('B,C',f.getTargetColumn())
		assertEquals('PARENT2_B,PARENT2_C',f.getSourceColumn())
 	}
	
	@Test
	void testGetRefferenceColumnList() {
		assertEquals(getRefferenceColumnList(),"A,B,C,PARENT1_B,PARENT1_C,PARENT2_B,PARENT2_C,PARENT3_A")
	}
	
	

}
