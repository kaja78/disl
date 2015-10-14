/*
 * Copyright 2015 Karel Hübl <karel.huebl@gmail.com>.
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
import org.disl.pattern.TablePattern
import org.disl.pattern.generic.CreateOrReplaceTablePattern
import org.junit.Before
import org.junit.Test


@Indexes(@Index(columns=["A","B"]))
class TestTable extends Table {

	CreateOrReplaceTablePattern pattern
	
	@Description("Column A.")
	@DataType("VARCHAR(255)")
	Column A
	
	@DataType("VARCHAR(255)")
	Column B
	
	@DataType("VARCHAR(255)")
	Column C
	
	
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
	
	

}
