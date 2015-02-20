package org.disl.meta

import javax.management.InstanceOfQueryExp;


class Mapping  extends MappingSource implements Initializable {
		
	List columns=[]
	List<MappingSource> sources=[]
	String filter="1=1"
	String groupBy
	
	protected Mapping(){}

	Closure getFilter() {
		return {"1=1"}
	}
	
	Closure getGroupBy() {
		return {null}
	}
	
	Closure getHaving() {
		return {null}
	}
	
	public void init() {
		initNullProperties()
		initSourceAliases()
		initColumnAliases()
		initJoins()
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
		getPropertyNamesByType(ColumnMapping).each { initPropertyAlias(it)}
	}
	
	void initPropertyAlias(String property) {
		getProperty(property).alias=property
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
	
	void initJoins(){
		//Empty hook.
	}
	
	public void from (MappingSource source) {
		sources.add(source)
	}
	public MappingSource innerJoin (MappingSource source) {
		source.join=new Join.INNER(source:source)
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
			${getSources().each({it}).join(",\n			")}
		WHERE
			${filter}
		${getGroupByClause()}
	/*End of mapping $name*/
"""
	}
	
	String getQueryColumnList() {
		getColumns().each {"${it}"}.join(",\n			")
	}
	
	String getGroupByClause() {
		if  (groupBy!=null) {
			return """GROUP BY
			${groupBy}"""
		}
		return ""
	}
	
}
