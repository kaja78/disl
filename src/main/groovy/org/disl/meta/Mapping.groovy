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
package org.disl.meta

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.lang.reflect.Field
import java.sql.ResultSetMetaData

import org.disl.pattern.Executable
import org.disl.pattern.ExecutionInfo
import org.disl.pattern.MappingPattern

import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Defines data transformation which can be executed as SQL query.
 * 
 * This class implements early initialization for all fields of types assignable from MappingSource.
 * This enables to reference MappingSources in initializers of ColumnMapping fields.
 * */
@CompileStatic
abstract class Mapping  extends MappingSource implements Initializable,Executable {
	/**
	 * Since this is first field in this class definition, it ensures doEarlyInit() method is called before next fields are intialized.
	 * */
	private boolean earlyInitialized = doEarlyInit()
	private String groupBy
	private String orderBy

	List<ColumnMapping> columns = []
	List<MappingSource> sources = []
	List<MappingSource> withSources = []
	List<SetOperation> setOperations = []
	List<BindVariable> bindVariables=[]
	String filter = "1=1"

	public boolean isEarlyInitialized() {
		return earlyInitialized
	}

	public MappingPattern getPattern() {
		return null
	}

	public String getSchema() {
		'default'
	}

	protected Mapping(){}

	/**
	 * Implements early initialisation.
	 * */
	protected boolean doEarlyInit() {
		super.init()
		initSourceAliases()
		return true
	}

	public void init() {
		initColumnAliases()
		initMapping()
		initColumnDescription()
		if (getGroupBy()==null && getColumns().find({it instanceof AggregateColumnMapping})) {
			groupBy()
		}
		initPattern()
	}
	
	protected void initPattern() {
		initPatternField()
	}

	private initPatternField() {
		if (!getPattern()) {
			Field patternField =getFieldByName('pattern')
			if (patternField) {
				patternField.setAccessible(true)
				patternField.set(this, MetaFactory.create((Class<MappingPattern>)patternField.getType(),
						{((MappingPattern)it).setMapping(this
						)}))
			}
		}
	}

	protected void initColumnDescription() {
		getFieldsByType(ColumnMapping).each {
			Description desc =it.getAnnotation(Description)
			if (desc) {
				((ColumnMapping)this[it.getName()]).setDescription(desc.value())
			}
		}
	}

	protected String getGroupBy() {
		groupBy
	}

	@Override
	public String getRefference() {
		if (sourceWithClause) {
			return sourceAlias
		}
		if (sourceAlias != null) {
			return "(\n${getSQLQuery()}) $sourceAlias"
		}
		return "(${getSQLQuery()})"
	}

	Closure getHaving() {
		return {null }
	}

	protected void initColumnAliases() {
		getFieldsByType(ColumnMapping).each { initColumnMapping(it) }
	}

	protected void initColumnMapping(Field field) {
		Name name=field.getAnnotation(Name)
		if (name) {
			((ColumnMapping) this[field.name]).alias = name.value()
		} else {
			((ColumnMapping) this[field.name]).alias = field.name
		}
	}

	void initSourceAliases() {
		getPropertyNamesByType(MappingSource).each { initSourceAlias(it) }
	}

	void initSourceAlias(String property) {
		MetaProperty metaProperty = this.getMetaClass().getProperties().find { it.name == property }
		MappingSource p = (MappingSource) MetaFactory.create(metaProperty.getType())
		p.sourceAlias = property
		this[property] = p
	}

	abstract void initMapping();

	public void with(MappingSource... src) {
		src.each { MappingSource s ->
			if (s instanceof Table) {
				throw new IllegalArgumentException('Table not allowed in with clause. Use Mapping or Lookup.')
			}
			s.setSourceWithClause(true)
			withSources.add(s)
		}
	}

	public void from(MappingSource source) {
		sources.add(source)
	}

	public MappingSource innerJoin(MappingSource source) {
		source.join = new Join.INNER(source: source)
		sources.add(source)
		source
	}

	public MappingSource leftOuterJoin(MappingSource source) {
		source.join = new Join.LEFT(source: source)
		sources.add(source)
		source
	}

	public MappingSource rightOuterJoin(MappingSource source) {
		source.join = new Join.RIGHT(source: source)
		sources.add(source)
		source
	}

	public MappingSource fullOuterJoin(MappingSource source) {
		source.join = new Join.FULL(source: source)
		sources.add(source)
		source
	}

	public MappingSource cartesianJoin(MappingSource source) {
		source.join = new Join.CARTESIAN(source: source)
		sources.add(source)
		source
	}

	public void where(String condition) {
		filter = condition
	}

	/**
	 * Explicitly generate groupBy clause for all expression mappings.
	 * */
	public void groupBy() {
		groupBy(getColumns().findAll { it instanceof ExpressionColumnMapping })
	}

	public void groupBy(Object... expressions) {
		ArrayList l = new ArrayList(expressions.length)
		l.addAll(expressions)
		groupBy(l)
	}

	public void groupBy(Collection expressions) {
		String clause = expressions.collect({
			if (it instanceof ExpressionColumnMapping) {
				it = it.expression
			}
			it.toString()
		}).join(',')
		groupBy(clause)
	}

	public void groupBy(String clause) {
		groupBy = clause
	}

	/**
	 * Explicitly generate orderBy clause for all expression mappings.
	 * */
	public void orderBy() {
		orderBy(getColumns().findAll { it instanceof ExpressionColumnMapping })
	}

	public void orderBy(Object... expressions) {
		ArrayList l = new ArrayList(expressions.length)
		l.addAll(expressions)
		orderBy(l)
	}

	public void orderBy(Collection expressions) {
		String clause = expressions.collect({
			if (it instanceof ExpressionColumnMapping) {
				it = it.expression
			}
			it.toString()
		}).join(',')
		orderBy(clause)
	}

	public void orderBy(String clause) {
		orderBy = clause
	}

	public void union(MappingSource source) {
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
			value = "'${value}'"
		}
		return new SqlExpression(expression: value)
	}

	/**
	 * Shorthand for createExpressionColumnMapping.
	 * */
	ColumnMapping e(String expression) {
		createExpressionColumnMapping(expression)
	}

	ColumnMapping e(Integer expression) {
		e(expression.toString())
	}

	ColumnMapping e(Double expression) {
		e(expression.toString())
	}

	ColumnMapping e(AbstractSqlExpression expression) {
		e(expression.toString())
	}

	ColumnMapping createExpressionColumnMapping(String expression) {
		addColumnMapping new ExpressionColumnMapping(expression: expression, parent: this)
	}

	ColumnMapping addColumnMapping(ColumnMapping columnMapping) {
		columns.add columnMapping
		columnMapping
	}

	/**
	 * Shorthand for createAggregateColumnMapping.
	 * */
	ColumnMapping a(String aggregateFunction) {
		createAggregateColumnMapping(aggregateFunction)
	}

	ColumnMapping a(Integer aggregateFunction) {
		a(aggregateFunction.toString())
	}

	ColumnMapping a(Double aggregateFunction) {
		a(aggregateFunction.toString())
	}

	ColumnMapping a(AbstractSqlExpression aggregateFunction) {
		a(aggregateFunction.toString())
	}

	ColumnMapping createAggregateColumnMapping(String aggregateFunction) {
		addColumnMapping new AggregateColumnMapping(expression: aggregateFunction, parent: this)
	}

	String getSQLQuery() {
		"""\
	/*Mapping ${name}*/${getWithClause()}
		SELECT
			${getQueryColumnList()}
		FROM
			${getSources().collect({ it.fromClause }).join("\n			")}
		WHERE
			${filter}
		${getGroupByClause()}${getOrderByClause()}${getSetOperationClause()}
	/*End of mapping $name*/"""
	}

	String getQueryColumnList() {
		getColumns().collect { "${it.getAliasedMappingExpression()}" }.join(",\n			")
	}

	String getRefferenceColumnList() {
		getColumns().collect { "${it.alias}" }.join(",")
	}

	Collection<String> getTargetColumnNames() {
		getColumns().collect({ it.alias })
	}

	String getGroupByClause() {
		if (groupBy != null) {
			return """GROUP BY
			${groupBy}"""
		}
		return ""
	}

	String getOrderByClause() {
		if (orderBy != null) {
			return """
		ORDER BY
			${orderBy}"""
		}
		return ""
	}

	String getSetOperationClause() {
		if (setOperations.size() > 0) {
			return "\n\t" + setOperations.collect { it.getSetOperationClause() }.join("\n\t")
		}
		return ""
	}

	/**
	 * Validate sql query in database. This is processed by preparing jdbc statement containing mapping sql query.
	 * */
	public void validate() {
		PhysicalSchema physicalSchema = Context.getContext().getPhysicalSchema(getSchema())
		physicalSchema.validateQuery(this)
	}

	public Sql getSql() {
		Context.getSql(getSchema())
	}

	@Override
	public void execute() {
		if (pattern) {
			pattern.execute()
		} else {
			println exportToCSV(100)
		}
	}

	@Override
	public void simulate() {
		if (pattern) {
			pattern.simulate()		
		} else {
			println getSQLQuery()
		}
	}

	@Override
	public ExecutionInfo getExecutionInfo() {
		if (pattern) {
			return pattern.executionInfo
		}
		return new ExecutionInfo();
	}
	
	public String exportToCSV(int maxRows) {
		StringBuffer csv=new StringBuffer()
		getSql().rows(getSQLQuery(),1,maxRows,{csv.append(getCSVHeader((ResultSetMetaData)it))}).each {csv.append(((GroovyRowResult)it).values().join('\t')+'\n')}
		csv.toString()
	}
	
	private String getCSVHeader(ResultSetMetaData metaData) {
		List l=[]
		for (int i= 1; i <= metaData.getColumnCount(); i++) {
			l.add(metaData.getColumnName(i))
		}
		l.join('\t') + '\n'
	}

	public void copySqlQueryToClipboard() {
		StringSelection ss = new StringSelection(getClipboardQuery());
		try {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
		} catch (Exception e) {
		}
	}

	protected String getClipboardQuery() {
		replaceBindVariables(getSQLQuery())
	}

	protected String replaceBindVariables(String sqlQuery) {
		bindVariables.each {sqlQuery=sqlQuery.replaceAll("/\\*BIND\\*/$it.name", Matcher.quoteReplacement("/*$it.name*/$it.value"))}
		return sqlQuery
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	public void traceInitialColumnMapping() {
		if (columns.size() == 0) {
			sources.each {
				it.columns.each { println getInitialMapping(it.name) }
			}
		}
	}

	String getInitialMapping(String columnName) {
		"ColumnMapping $columnName=e ${findSourceAlias(columnName)}.$columnName"
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	String findSourceAlias(String columnName) {
		String sourceAlias = 'src'
		getSources().each {
			def c = it.columns.find { equals(columnName, it) }
			if (c) {
				sourceAlias = it.sourceAlias
			}
		}
		return sourceAlias
	}

	boolean equals(String columnName, Column column) {
		columnName.equals(column.name)
	}

	boolean equals(String columnName, ColumnMapping columnMapping) {
		columnName.equals(columnMapping.alias)
	}

	@Override
	String getWithReference() {
		return "${sourceAlias} as (${getSQLQuery()})"
	}

	String getWithClause() {
		if (withSources.isEmpty()) {
			return ''
		}
		"\n\twith${withSources.collect({"\n\t${it.withReference}"}).join(',')}"
	}

	BindVariable bind(String bindVariableName, String dataType, def value) {
		BindVariable bindValue=new BindVariable(bindVariableName,dataType,value)
		bindVariables.add(bindValue)
		return bindValue
	}



	class BindVariable {
		String name
		def dataType
		def value

		BindVariable(String name, String dataType, def value) {
			this.name=name
			this.dataType=dataType
			this.value=value
		}

		@Override
		String toString() {
			return "/*BIND*/$name"
		}
	}
}