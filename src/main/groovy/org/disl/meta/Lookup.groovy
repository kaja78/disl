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

/**
 * Creates logical SQL query containing constant record set, which can be used as MappingSource.
 * This enables constant lookup definition in DISL model without the need to deploy any database objects to database.
 * */
@CompileStatic
abstract class Lookup extends MappingSource {
	List<Column> columns

	abstract List<List> getRecords();
	
	public String getSchema() {
		'default'
	}

	public PhysicalSchema getPhysicalSchema() {
		Context.getContext().getPhysicalSchema(getSchema())
	}
	
	public void init() {
		super.init()
		columns=new LinkedList<Column>()
		initColumns()
	}

	protected void initColumns() {
		getFieldsByType(Column).each {initColumn(it)}
	}

	protected void initColumn(Field f) {
		Column column=(Column)this[f.getName()]

		if (column==null) {
			column=(Column)f.getType().newInstance()
			column.parent=this
			this[f.getName()]=column
		}
		column.setName(f.getName());
		columns.add(column)
	}


	public String getRefference(){
		if (sourceWithClause) {
			return sourceAlias
		}
		if (sourceAlias!=null) {
			return "(\n${getLookupQuery()}) $sourceAlias"
		}
		return "(${getLookupQuery()})"
	}

	public String getLookupQuery() {
		"""\
	/*Lookup $name*/
	select * from 
		${physicalSchema.recordsToSubquery(createRecordsFromList())}
	/*End of lookup $name*/"""

	}
	
	String getRefferenceColumnList() {
		getColumns().collect {"${it.name}"}.join(",")
	}

	protected List<Map> createRecordsFromList() {
		List<List> recordList=getRecords()
		List<Map> result=(List<Map>)recordList.collect { values->
			Map record=new LinkedHashMap()
			columns.each {column ->
				record.put(column.name, values[columns.indexOf(column)])
			}
			return record
		}
		return result
	}

	@Override
	String getWithReference() {
		"$sourceAlias as (${getLookupQuery()})"
	}
}
