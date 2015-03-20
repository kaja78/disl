package org.disl.meta

import static org.junit.Assert.*
import groovy.sql.Sql

import java.lang.reflect.Field
import java.sql.SQLException

import org.junit.Before
import org.junit.Test



abstract class Mapping  extends MappingSource implements Initializable {
	abstract String getSchema()
	List columns=[]
	List<MappingSource> sources=[]

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

	

		
	/**
	 * Shorthand for createExpressionColumnMapping.
	 * */
	ExpressionColumnMapping e(expression) {
		createExpressionColumnMapping(expression)
	}

	ExpressionColumnMapping createExpressionColumnMapping(expression) {
		addColumnMapping new ExpressionColumnMapping(expression)
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
		addColumnMapping new AggregateColumnMapping(aggregateFunction)
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
			${getSources().each({it}).join("\n			")}
		WHERE
			${filter}
		${getGroupByClause()}
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
	
	@Test
	public void testValidateMapping() {
		validate()
	}
	
	/**
	 * Validate sql query in database. This is processed by preparing jdbc statement containing mapping sql query.
	 * */
	public void validate() {
		try {
			getSql().getConnection().prepareStatement("select * from (${getSQLQuery()}) where 1=2").execute()			
		} catch (SQLException e) {		
			throw new AssertionError("Validation failed with message: ${e.getMessage()} for query:\n${getSQLQuery()}")
		}		
	}
	
	public Sql getSql() {
		Context.getSql(getSchema())
	}
	

	
}
