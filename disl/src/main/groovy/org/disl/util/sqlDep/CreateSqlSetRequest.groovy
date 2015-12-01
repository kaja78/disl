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
			queries.add new SqlDepQuery(sourceObject: mapping,sourceCode: "CREATE VIEW ${Context.getPhysicalSchemaName('default')}.${mapping.name} as ${sqlQuery};")
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
