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

import groovy.transform.CompileStatic

import java.lang.reflect.Field

@CompileStatic class ForeignKeyMeta {
	String name		
	Table targetTable
	String targetTableClassName
	
	List<String> sourceColumns=[]
	List<String> targetColumns=[]
	
	public void setTargetTable(Table targetTable) {			
		this.targetTable=targetTable
		setTargetTableClassName(targetTable.getClass().getName())						
	}
	
	public void setSourceColumn(String name) {
		sourceColumns=Arrays.asList(name.split(','))
	}
	
	public String getSourceColumn() {
		sourceColumns.join(',')	
	}
	
	public void setTargetColumn(String name) {
		targetColumns=Arrays.asList(name.split(','))
	}
	
	public String getTargetColumn() {
		targetColumns.join(',')
	}
	
	static void initForeignKey(Table table) {
		table.getFieldsByType(Column).each {
			Field f=it
			ForeignKey foreignKey=f.getAnnotation(ForeignKey)
			if (foreignKey!=null) {
				ForeignKeyMeta.initForeignKey(foreignKey, table, (Column)table[f.getName()])
			}
		}
		
		ForeignKeys foreignKeys=table.getClass().getAnnotation(ForeignKeys)
		if (foreignKeys!=null) {
			foreignKeys.value().each {ForeignKeyMeta.initForeignKey((ForeignKey)it,table,null)}
		}
	}
					
	static void initForeignKey(ForeignKey foreignKey,Table table,Column column) {
		//Hence foreign keys may build circular dependencies in DISL data model, foreign key target table may not be fully initialized.
		Table targetTable=MetaFactory.newInstance(foreignKey.targetTable())
		targetTable.initColumns()
		
		table.getForeignKeys().add new ForeignKeyMeta(
			name: foreignKey.name(),
			sourceColumn: column==null ? foreignKey.sourceColumn() : column.name,
			targetTable: targetTable,
			targetColumn: foreignKey.targetColumn()=='' ? targetTable.getPrimaryKeyColumns().join(',') : foreignKey.targetColumn()
			)
	}
}