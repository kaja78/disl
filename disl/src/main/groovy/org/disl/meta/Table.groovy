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

import java.lang.reflect.Field;
import java.util.List;

import org.disl.pattern.Executable;
import org.disl.pattern.ExecutionInfo;
import org.disl.pattern.Pattern;
import org.disl.pattern.TablePattern;

/**
 * Representation of table or view in DISL data model.
 * */
abstract class Table extends MappingSource implements  Executable, IndexOwner, Initializable {

	public abstract TablePattern getPattern()

	List<Column> columns=[]
	String description=""
	List<IndexMeta> indexes=[]
	List<UniqueKeyMeta> uniqueKeys=[]
	List<ForeignKeyMeta> foreignKeys=[]


	public String getSchema() {
		'default'
	}
	
	@Override
	public String getRefference() {
		String alias=""
		if (sourceAlias!=null) {
			alias=" ${sourceAlias}"
		}
		return "${fullName}${alias}"
	}

	public String getFullName() {
		String ownerPrefix=""
		String owner=getPhysicalSchema()
		if (owner!=null) {
			ownerPrefix="${owner}."
		}
		"${ownerPrefix}${name}"
	}

	public String getPhysicalSchema() {
		Context.getPhysicalSchemaName(getSchema())
	}

	protected Column c(String name) {
		createColumn(name)
	}

	private Column createColumn(String name) {
		Column c=new Column(name, this)
		columns.add(c)
		c
	}

	public void execute() {
		getPattern().execute()
	}

	public void simulate() {
		getPattern().simulate()
	}

	public ExecutionInfo getExecutionInfo(){
		return getPattern().getExecutionInfo()
	}

	public void init() {
		initColumns()
		initConstraints()

		Description desc=this.getClass().getAnnotation(Description)
		if (desc!=null) {
			description=desc.value()
		}

		IndexMeta.initIndexes(this)
		UniqueKeyMeta.initUniqueKeys(this)
		

		initPattern()
	}

	protected void initPattern() {
		initPatternField()
	}

	private initPatternField() {
		if (!getPattern()) {
			Field patternField=getFieldByName('pattern')
			if (patternField) {
				patternField.setAccessible(true)
				patternField.set(this,MetaFactory.create(patternField.getType(),{((TablePattern<Table>)it).setTable(this)}))
			}
		}
	}

	protected void initColumns() {
		getFieldsByType(Column).each {initColumn(it)}
	}

	protected void initConstraints() {
		getClass().getFields().findAll({it.getAnnotation(UniqueKey)!=null})
		
		ForeignKeys foreignKeys=getClass().getAnnotation(ForeignKeys)
		if (foreignKeys!=null) {
			foreignKeys.value().each {ForeignKeyMeta.initForeignKey(it,this,null)}			
		}
	}

	protected void initColumn(Field f) {
		Column column=this[f.getName()]

		if (column==null) {
			column=f.getType().newInstance()
			column.parent=this
			this[f.getName()]=column
			columns.add(column)
		}
		column.setName(f.getName());

		Description desc=f.getAnnotation(Description)
		if (desc!=null) {
			column.setDescription(desc.value())
		}

		DataType dataType=f.getAnnotation(DataType)
		if (dataType!=null) {
			column.setDataType(dataType.value())
		}

		PrimaryKey primaryKey=f.getAnnotation(PrimaryKey)
		if (primaryKey!=null) {
			column.setPrimaryKey(true)
		}

		DefaultMapping defaultMapping=f.getAnnotation(DefaultMapping)
		if (defaultMapping!=null) {
			column.setDefaultMapping(defaultMapping.value())
		}

		ForeignKey foreignKey=f.getAnnotation(ForeignKey)
		if (foreignKey!=null) {
			ForeignKeyMeta.initForeignKey(foreignKey, this, column)
		}

		NotNull notNull=f.getAnnotation(NotNull)
		if (notNull!=null) {
			column.setNotNull(true)
		}
	}

	public Iterable<String> getColumnDefinitions() {
		columns.collect {it.columnDefinition}
	}
	
	public List<Column> getPrimaryKeyColumns() {
		columns.findAll {it.isPrimaryKey()}
	}

	static class ForeignKeyMeta {
		String name		
		Table targetTable
		List<String> sourceColumns=[]
		List<String> targetColumns=[]
		
		public void setSourceColumn(String name) {
			sourceColumns=name.split(',')
		}
		
		public String getSourceColumn() {
			sourceColumns.join(',')	
		}
		
		public void setTargetColumn(String name) {
			targetColumns=name.split(',')
		}
		
		public String getTargetColumn() {
			targetColumns.join(',')
		}
						
		static void initForeignKey(ForeignKey foreignKey,Table table,Column column) {
			Table targetTable=foreignKey.targetTable().equals(table.getClass()) ? table : MetaFactory.create(foreignKey.targetTable())
			table.getForeignKeys().add new ForeignKeyMeta(
				name: foreignKey.name(),
				sourceColumn: column==null ? foreignKey.sourceColumn() : column.name,
				targetTable: targetTable,
				targetColumn: foreignKey.targetColumn()=='' ? targetTable.getPrimaryKeyColumns().join(',') : foreignKey.targetColumn()
				)
		}
	}
}
