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

import groovy.sql.Sql

import java.lang.reflect.Field
import java.sql.SQLException


/**
 * Defines data transformation which can be executed as SQL query.
 * 
 * This class implements early initialization for all fields of types assignable from MappingSource.
 * This enables to reference MappingSources in initializers of ColumnMapping fields.
 * */
abstract class Mapping  extends MappingSource implements Initializable {
	/**
	 * Since this is first field in this class definition, it ensures doEarlyInit() method is called before next fields are intialized.
	 * */
	private boolean earlyInitialized=doEarlyInit()
	private String groupBy

	String description
	List<ColumnMapping> columns=[]
	List<MappingSource> sources=[]
	List<SetOperation> setOperations=[]
	String filter="1=1"

	public boolean isEarlyInitialized() {
		return earlyInitialized
	}

	public abstract String getSchema()

	protected Mapping(){}

	/**
	 * Implements early initialisation.
	 * */
	protected boolean doEarlyInit() {
		initSourceAliases()
		return true
	}

	public void init() {
		initColumnAliases()
		initMapping()
		initDescription()
		if (getGroupBy()==null && getColumns().find({it instanceof AggregateColumnMapping})) {
			groupBy()
		}
	}

	protected void initDescription() {
		Description desc=this.getClass().getAnnotation(Description)
		if (desc) {
			setDescription(desc.value())
		}

		getFieldsByType(ColumnMapping).each {
			desc=it.getAnnotation(Description)
			if (desc) {
				this[it.getName()].setDescription(desc.value())
			}
		}
	}

	protected String getGroupBy() {
		groupBy
	}

	@Override
	public String getRefference() {
		if (sourceAlias!=null) {
			return "(\n${getSQLQuery()}) $sourceAlias"
		}
		return "(${getSQLQuery()})"
	}

	Closure getHaving() {
		return {null}
	}

	void initColumnAliases() {
		getFieldsByType(ColumnMapping).each { initAlias(it)}
	}

	void initAlias(Field field) {
		this[field.name].alias=field.name
	}

	void initSourceAliases() {
		getPropertyNamesByType(MappingSource).each {initSourceAlias(it)}
	}

	void initSourceAlias(String property) {
		MetaProperty metaProperty=metaClass.getProperties().find {it.name==property}
		def p=MetaFactory.create(metaProperty.getType())
		p.sourceAlias=property
		this[property]=p
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

	/**
	 * Explicitly generate groupBy clause for all expression mappings.
	 * */
	public void groupBy() {
		groupBy(getColumns().findAll {it instanceof ExpressionColumnMapping})
	}

	public void groupBy(Object... expressions) {
		ArrayList l=new ArrayList(expressions.length)
		l.addAll(expressions)
		groupBy(l)
	}

	public void groupBy(List expressions) {
		String clause=expressions.collect({
			if (it instanceof ExpressionColumnMapping) {
				it=it.expression
			}
			it.toString()
		}).join(',')
		groupBy(clause)
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

	SqlExpression constant(Object value) {
		return createConstant(value)
	}

	SqlExpression createConstant(Object value) {
		if (value instanceof String || value instanceof GString) {
			value="'${value}'"
		}
		return new SqlExpression(expression:value)
	}


	/**
	 * Shorthand for createExpressionColumnMapping.
	 * */
	ExpressionColumnMapping e(String expression) {
		createExpressionColumnMapping(expression)
	}

	ExpressionColumnMapping e(Integer expression) {
		e(expression.toString())
	}

	ExpressionColumnMapping e(Double expression) {
		e(expression.toString())
	}

	ExpressionColumnMapping e(AbstractSqlExpression expression) {
		e(expression.toString())
	}

	ExpressionColumnMapping createExpressionColumnMapping(String expression) {
		addColumnMapping new ExpressionColumnMapping(expression: expression,parent: this)
	}

	ColumnMapping addColumnMapping(columnMapping) {
		columns.add columnMapping
		columnMapping
	}

	/**
	 * Shorthand for createAggregateColumnMapping.
	 * */
	AggregateColumnMapping a(String aggregateFunction) {
		createAggregateColumnMapping(aggregateFunction)
	}

	AggregateColumnMapping a(Integer aggregateFunction) {
		a(aggregateFunction.toString())
	}

	AggregateColumnMapping a(Double aggregateFunction) {
		a(aggregateFunction.toString())
	}

	AggregateColumnMapping a(AbstractSqlExpression aggregateFunction) {
		a(aggregateFunction.toString())
	}

	AggregateColumnMapping createAggregateColumnMapping(String aggregateFunction) {
		addColumnMapping new AggregateColumnMapping(expression: aggregateFunction,parent: this)
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
		getColumns().collect {"${it.getAliasedMappingExpression()}"}.join(",\n			")
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

	/**
	 * Validate sql query in database. This is processed by preparing jdbc statement containing mapping sql query.
	 * */
	public void validate() {
		try {
			String sqlStatement = "select * from (${getSQLQuery()}) where 1=2"
			println sqlStatement
			getSql().rows(sqlStatement)
		} catch (SQLException e) {
			throw new AssertionError("Validation failed with message: ${e.getMessage()} for query:\n${getSQLQuery()}")
		}
	}

	public Sql getSql() {
		Context.getSql(getSchema())
	}
}
