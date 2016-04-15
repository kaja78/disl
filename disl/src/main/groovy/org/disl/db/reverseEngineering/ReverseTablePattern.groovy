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
package org.disl.db.reverseEngineering


import org.disl.meta.Column
import org.disl.meta.Table
import org.disl.pattern.FileOutputStep
import org.disl.pattern.Pattern
import org.disl.pattern.TablePattern
/**
 * Pattern for generating source code for DISL data model table.
 * */
class ReverseTablePattern extends TablePattern<ReverseEngineeredTable> {
	/**
	 * Output directory for generated source code.
	 * */
	File outputDir=new File("src")
	
	/**
	 * Package name of generated table class.
	 * */
	String packageName
	
	/**
	 * Class name of table class parent.
	 * */
	String parentClassName
	
	File getFile() {
		File directory=new File(outputDir,packageName.replace('.', '/'))
		new File(directory,"${table.name}.groovy")
	}

	@Override
	public void init() {	
		add CreateDislTable
	}
	
	static class CreateDislTable extends FileOutputStep {
		
		ReverseTablePattern getPattern() {
			super.pattern
		}
		
		File getFile() {
			getPattern().getFile()
		}
		
		String getCode() {
			"""\
package $pattern.packageName

import org.disl.meta.*

@Description(\"""$pattern.table.description\""")$foreignKeyDefinition
class $pattern.table.name extends ${pattern.parentClassName} {

$columnDefinitions
}"""
	}

	String getColumnDefinitions() {
		pattern.table.getColumns().collect {getColumnDefinitionCode(it)}.join("\n\n")
	}
	
	String getForeignKeyDefinition() {
		if (pattern.table.foreignKeys.size()==0) {
			return ''
		}
		return """
@ForeignKeys([${pattern.table.foreignKeys.collect({"@ForeignKey(name='${it.name}',targetTable=${it.targetTableClassName},sourceColumn='${it.sourceColumn}',targetColumn=('${it.targetColumn}'))"}).join(',\n')}])"""
	}

	String getColumnDefinitionCode(Column column) {
		String notNull =column.notNull?"\n\t\t@NotNull":""
		String primaryKey =column.primaryKey?"\n\t\t@PrimaryKey":""
		
		"""\
		@Description(\"""$column.description\""")
		@DataType("$column.dataType")$primaryKey$notNull
		Column $column.name"""
		}
	
		
		
	}


}








 