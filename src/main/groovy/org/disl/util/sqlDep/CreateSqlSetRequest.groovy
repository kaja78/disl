/*
 *
 * Copyright 2015 - 2021 Karel Hübl <karel.huebl@gmail.com>.
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
package org.disl.util.sqlDep

import groovy.json.JsonBuilder

import org.disl.meta.Context
import org.disl.meta.Mapping
import org.disl.meta.Table
import org.disl.meta.TableMapping
import org.disl.pattern.TablePattern

class CreateSqlSetRequest {
	String userAccountId
	String dialect='generic'
	String customSqlSetName=''
	List<SqlDepQuery> queries=[]
	
	private MetaStack stack=new MetaStack()

	String toJson() {
		new JsonBuilder( this ).toPrettyString();
	}

	public void add(Object... dislObject) {
		dislObject.each {addQuery(it)}
	}

	void addQuery(Mapping mapping) {
		if (stack.isInStack(mapping)) {
			return
		}

		addSources(mapping)
		replaceSourceMappingJoinsWithTable(mapping)

		String sqlQuery=mapping.getSQLQuery()

		if (mapping instanceof TableMapping) {
			queries.add new SqlDepQuery(sourceObject: mapping,sourceCode: "CREATE TABLE ${mapping.target.fullName} as ${sqlQuery}\n;")
		} else {
			queries.add new SqlDepQuery(sourceObject: mapping,sourceCode: "CREATE VIEW ${Context.getPhysicalSchemaName(mapping.getSchema())}.${mapping.name} as ${sqlQuery};")
		}
	}
	
	void replaceSourceMappingJoinsWithTable(Mapping mapping) {
		mapping.getSources().findAll({it instanceof Mapping}).each {
			Table t=new Table() {
						TablePattern pattern
					}
			t.name=it.name
			t.sourceAlias=it.sourceAlias
			it.join.source=t
		}
	}

	void addQuery(Table table) {
		if (stack.isInStack(table)) {
			return
		}
		queries.add new SqlDepQuery(sourceObject: table,sourceCode: "CREATE TABLE ${table.fullName} (${table.columns.collect({"$it.columnDefinition"}).join(',\n\t')});")
	}

	void addSources(Mapping mapping) {
		mapping.getSources().findAll({it instanceof Mapping || it instanceof Table}).each {
			addQuery(it)
		}
	}


	static class MetaStack {
		List<String> stack=[]
		
		boolean isInStack(def object) {
			String type=object.class.name
			if (stack.contains(type)) {
				return true
			}
			stack.add(type)
			return false
		}
	}
}
