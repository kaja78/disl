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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.disl.meta

import static org.junit.Assert.*
import groovy.sql.Sql

import java.lang.reflect.Field
import java.sql.SQLException

import javax.management.InstanceOfQueryExp;

import org.junit.Before
import org.junit.Test



abstract class Mapping  extends MappingSource implements Initializable {
	public abstract String getSchema()
	List columns=[]
	List<MappingSource> sources=[]
	List<SetOperation> setOperations=[]

	String filter="1=1"
	String groupBy
	
	protected Mapping(){}
	
	@Override
	public String getRefference() {
		if (sourceAlias!=null) {
			return "(\n${getSQLQuery()}) $sourceAlias"
		}
		return "(${getSQLQuery()})"
	}

	Closure getFilter() {
		return {"1=1"}
	}
	
	Closure getGroupBy() {
		return {null}
	}
	
	Closure getHaving() {
		return {null}
	}
	
	@Before
	public void init() {
		initNullProperties()
		initSourceAliases()
		initColumnAliases()
		initMapping()
	}
	
	void initNullProperties() {
		properties.findAll {it.value==null}.each {initNullProperty(it.key)}
	}
	
	void initNullProperty(String key) {
		
		MetaProperty property=this.metaClass.properties.find {it.name.equals(key)}
		def properyValue=MetaFactory.create(property.type)
		this[key]=properyValue
	}

	
	void initColumnAliases() {
		getFieldsByType(ColumnMapping).each { initAlias(it)}
	}
	
	void initAlias(Field field) {
		this[field.name].alias=field.name
	}
	
	void initSourceAliases() {
		getPropertyNamesByType(MappingSource).each {initPropertySourceAlias(it)}
	}
	
	void initPropertySourceAlias(String property) {
		def p=getProperty(property)
		if (p!=null) {
			p.sourceAlias=property
		}
	}
	
	abstract void initMapping();
	
	public void from (MappingSource source) {
		sources.add(source)
	}
	public MappingSource innerJoin (MappingSource source) {
		source.join=new Join.INNER(source:source)
		sources.add(source)
		source		
	}
	
	public MappingSource leftOuterJoin (MappingSource source) {
		source.join=new Join.LEFT(source:source)
		sources.add(source)
		source
	}
	
	public MappingSource rightOuterJoin (MappingSource source) {
		source.join=new Join.RIGHT(source:source)
		sources.add(source)
		source
	}

	public MappingSource fullOuterJoin (MappingSource source) {
		source.join=new Join.FULL(source:source)
		sources.add(source)
		source
	}
	
	public MappingSource cartesianJoin (MappingSource source) {
		source.join=new Join.CARTESIAN(source:source)
		sources.add(source)
		source
	}
	
	public void where(String condition) {
		filter=condition
	}
	
	public void groupBy(String clause) {
		groupBy=clause
	}
	
	public void union(Mapping source) {
		setOperations.add(new SetOperation.UNION(source: source))
	}
	
	public void unionAll(MappingSource source) {
		setOperations.add(new SetOperation.UNION_ALL(source: source))
	}
	
	public void minus(MappingSource source) {
		setOperations.add(new SetOperation.MINUS(source: source))
	
	}
	
	public void intersect(MappingSource source) {
		setOperations.add(new SetOperation.INTERSECT(source: source))	
	}
	
	SqlExpression constant(Object constant) {
		return createConstant(constant)
	}

	SqlExpression createConstant(Object constant) {
		return new SqlExpression(expression:constant)
	}

		
	/**
	 * Shorthand for createExpressionColumnMapping.
	 * */
	ExpressionColumnMapping e(expression) {
		createExpressionColumnMapping(expression)
	}

	ExpressionColumnMapping createExpressionColumnMapping(expression) {
		addColumnMapping new ExpressionColumnMapping(expression: expression,parent: this)
	}
	
	ColumnMapping addColumnMapping(columnMapping) {		
		columns.add columnMapping
		columnMapping
	}

	/**
	 * Shorthand for createAggregateColumnMapping.
	 * */
	AggregateColumnMapping a(aggregateFunction) {
		createAggregateColumnMapping(aggregateFunction)
	}

	AggregateColumnMapping createAggregateColumnMapping(aggregateFunction) {
		addColumnMapping new AggregateColumnMapping(expression: aggregateFunction,parent: this)
	}
	
	MappingSource src(Class<?> sourceType,Class<Join> joinType=Join.NONE,Closure condition={null}) {
		MappingSource source=sourceType.newInstance()
		Join j=joinType.newInstance()
		j.source=source
		j.conditionClosure=condition
		sources.add(source)
		return source
	}
	
	String getSQLQuery() {
		"""\
	/*Mapping ${name}*/
		SELECT
			${getQueryColumnList()}
		FROM
			${getSources().collect({it.fromClause}).join("\n			")}
		WHERE
			${filter}
		${getGroupByClause()}${getSetOperationClause()}
	/*End of mapping $name*/"""
	}
	
	String getQueryColumnList() {
		getColumns().collect {"${it.getMappingExpression()}"}.join(",\n			")
	}
	
	Collection<String> getTargetColumnNames() {
		getColumns().collect({it.alias})
	}
	
	String getGroupByClause() {
		if  (groupBy!=null) {
			return """GROUP BY
			${groupBy}"""
		}
		return ""
	}
	
	String getSetOperationClause() {
		if (setOperations.size()>0) {
			return "\n\t"+setOperations.collect {it.getSetOperationClause()}.join("\n\t")
		}
		return ""
	}
	
	@Test
	public void testValidateMapping() {
		validate()
	}
	
	/**
	 * Validate sql query in database. This is processed by preparing jdbc statement containing mapping sql query.
	 * */
	public void validate() {
		try {
			def sqlStatement = "select * from (${getSQLQuery()}) where 1=2"
			println sqlStatement
			getSql().getConnection().prepareStatement(sqlStatement).execute()			
		} catch (SQLException e) {		
			throw new AssertionError("Validation failed with message: ${e.getMessage()} for query:\n${getSQLQuery()}")
		}		
	}
	
	public Sql getSql() {
		Context.getSql(getSchema())
	}
	

	
}
