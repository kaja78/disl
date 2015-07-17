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
package org.disl.db.oracle

import java.lang.reflect.Field

import org.disl.meta.Column
import org.disl.meta.MappingSource
import org.disl.meta.UniqueKey


abstract class OracleLookup extends MappingSource {
	List<Column> columns
	
	private Object dummy=doEarlyInit()
	
	abstract List<List> getRecords();
	
	private Object doEarlyInit() {
		columns=new LinkedList<Column>()
		init()
		return null
	}
	
	public void init() {
		initColumns()
	}
	
	protected void initColumns() {
		getFieldsByType(Column).each {initColumn(it)}
	}
	
	protected void initColumn(Field f) {
		Column column=this[f.getName()]
		
		if (column==null) {
			column=f.getType().newInstance()
			column.parent=this
			this[f.getName()]=column
		}
		column.setName(f.getName());
		columns.add(column)
		
	}
	
	
	public String getRefference(){
		if (sourceAlias!=null) {
			return "(\n${getLookupQuery()}) $sourceAlias"
		}
		return "(${getLookupQuery()})"
	}
	
	public String getLookupQuery() {
		"select * from ${recordsToSubquery(createRecordsFromList())}"
	}
	
	protected List<Map> createRecordsFromList() {
		List recordList=getRecords()
		recordList.collect { values->
			Map record=new HashMap()
			columns.each {column ->
				record.put(column.name, values[columns.indexOf(column)])
			}
			return record
		}
		
	}
	
	public static String recordsToSubquery(List<Map> records) {
		String joinCondition=""
		List aliases=findAliases(records)
		boolean firstSource=true
		String sourceList=aliases.collect {
			String alias=it
			int index=0
			String innerQuery=records.collect {index++; mapToQuery(it,alias,index,firstSource)}.join("union all\n")
			firstSource=false
			return "(${innerQuery}) $alias"
		}.join(",\n")
		joinCondition=aliases.collect({"AND ${it}.DUMMY_KEY=${aliases[0]}.DUMMY_KEY"}).join("\n")
		return """${sourceList}
where
1=1
${joinCondition}"""
	}
	
	private static List findAliases(List<Map> records) {
		List aliases=[]
		records[0].keySet().each {
			String columnName=it.toString()
			if (columnName.contains('.')) {
				aliases.add(columnName.substring(0, columnName.indexOf('.')))
			}
		}
		if (aliases.size()==0) {
			aliases.add("SRC")
		}
		return aliases
	}
	
	private static String mapToQuery(Map row, String sourceAlias, int index,boolean includeMissingSourceAliasColumns) {
		Map sourceAliasRow=row.findAll {
			String key=it.key.toString()
			return key.startsWith("${sourceAlias}.") || (includeMissingSourceAliasColumns && !key.contains('.'))
		}
		sourceAliasRow=sourceAliasRow.collectEntries {key, value ->
			if (key.startsWith("${sourceAlias}.")) {
				key=key.substring(key.indexOf('.')+1)
			}
			[key, value]
		}
		String expressions=sourceAliasRow.collect({key, value -> "${value} as ${key}" }).join(",")
		return "select ${index} as DUMMY_KEY,${expressions} from dual\n"
	}
	
}
