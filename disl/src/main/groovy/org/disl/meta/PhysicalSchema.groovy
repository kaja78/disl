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
package org.disl.meta;

import groovy.sql.Sql
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.sql.SQLException

import org.disl.db.reverseEngineering.ReverseEngineeringService
import org.disl.workflow.DislScript
import org.junit.Assert

/**
 * Generic physical database schema. 
 * Context is used to map logical schemas used in DISL model to physical schemas.
 * Physical schemas are specific for each execution environment (Context).
 * */
@Slf4j
@CompileStatic 
public abstract class PhysicalSchema {
	
	String name
	String user
	String password
	String schema
	String databaseName
	SqlProxy sqlProxy
	
	abstract void setJdbcDriver(String driver)
	abstract String getJdbcDriver()
	abstract String getJdbcUrl()
	
	abstract String evaluateExpressionQuery(String expression)
	abstract String evaluateConditionQuery(String expression)
	abstract getRecordQuery(int index,String expressions);
	
	/**
	 * Evaluate value of SQL expression.
	 * */
	public Object evaluateExpression(def expression) {
		return getSql().firstRow(evaluateExpressionQuery(expression.toString())).getAt(0)
	}
	
	
	/**
	 * Evaluate value of SQL agregate expression.
	 * */
	public Object evaluateAggregateExpression(def expression,List<Map> records) {
		getSql().firstRow("select ${expression} from ${recordsToSubquery(records)}".toString()).getAt(0)
	}
	
	/**
	 * Evaluate rowcount returned by SQL query.
	 * */
	public long evaluateRowCount(String sqlQuery) {
		Long.parseLong(getSql().firstRow(getEvaluateRowCountQuery(sqlQuery)).getAt(0).toString())
	}


	protected String getEvaluateRowCountQuery(String sqlQuery) {
		"select count(1) from (${sqlQuery}) as s"
	}
	/**
	 * Validate SQL query. Throw exception for invalid query
	 * */
	public void validateQuery(String sqlQuery) throws AssertionError {
		try {
			getSql().rows(getValidationQuery(sqlQuery))
		} catch (SQLException e) {
			throw new AssertionError("Validation failed with message: ${e.getMessage()} for query:\n${sqlQuery}")
		}
	}
	
	protected String getValidationQuery(String sqlQuery) {
		"select * from (${sqlQuery}) as s where 1=2"
	}

	
	public void init() {
		Context context=Context.getContext();
		user=getSchemaProperty("user",user)		
		jdbcDriver=getSchemaProperty("jdbcDriver",jdbcDriver)
		schema=getSchemaProperty("schema",schema)
		databaseName=getSchemaProperty("databaseName", databaseName)
		initPassword()
	}
	
	protected String initPassword() {
		password=getSchemaProperty("password",password)
		String encodedPassword=getSchemaProperty("encodedPassword")
		if (encodedPassword) {
			password=DislScript.decode(encodedPassword)
		}		
	}
	
	protected String getSchemaProperty(String key, String defaultValue) {
		Context.getContext().getProperty("${name}.${key}",defaultValue)
	}
	
	protected String getSchemaProperty(String key) {
		Context.getContext().getProperty("${name}.${key}")
	}
	
	public Sql getSql(){
		if (sqlProxy==null || (sqlProxy.sql.connection==null || sqlProxy.sql.connection.isClosed())) {
			sqlProxy=createSqlProxy()	
		}
		sqlProxy.sql
	}

	protected SqlProxy createSqlProxy() {
		def sql=createSql()
		return new SqlProxy(sql: sql)
	}
	
	protected Sql createSql() {
		def sql=Sql.newInstance(getJdbcUrl(), getUser(), getPassword(), getJdbcDriver())
		sql.getConnection().setAutoCommit(false)
		sql.cacheConnection=true
		sql.cacheStatements=false
		log.info("${name} - Created new jdbcConnection for url: ${getJdbcUrl()}")
		return sql
	}
	
	public String recordsToSubquery(List<Map> records) {
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

	private List findAliases(List<Map> records) {
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

	public String mapToQuery(Map<String,String> row, String sourceAlias, int index,boolean includeMissingSourceAliasColumns) {
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
		return getRecordQuery(index,expressions)
	}
	
	
	String getColumnDefinition(Column column) {
		"${column.getName()} ${column.getDataType()}${getDefaultValueClause(column)}${getNotNullConstraint(column)}${getCheckConstraint(column)}"
	}
	
	String getNotNullConstraint(Column column) {
		if (column.isNotNull()) {
			return " NOT NULL"
		}
		return ''
	}
	
	
	String getDefaultValueClause(Column column) {
		if (column.getDefaultValue()==null) {
			return ''
		}
		return " DEFAULT ${column.getDefaultValue()}"
		
	}
	
	String getCheckConstraint(Column column) {
		if (column.getCheck()) {
			return " CHECK (${column.getCheck()})"
		}
		return ''
	}
	
	public ReverseEngineeringService getReverseEngineeringService() {
		return new ReverseEngineeringService()
	}
	
	public void validateTableDeployment(Table table,String tableType='TABLE',Sql sql=getSql()) {
		List<Table> tables=getReverseEngineeredTables(table.physicalSchema,table.name,tableType,sql)
		if (!tables ||tables.size()==0) {
			throw new AssertionError("Table ${table.getRefference()} not deployed in database.")
		} else if (tables.size()>1) {
			throw new AssertionError("Multiple tables matching ${table.refference} deployed in database.")
		}
		Assert.assertEquals("Table comment of deployed ${table.refference} does not match to model.",table.description ?: '',tables[0].description ?: '')
		validateTableColumns(tables[0],table)
	}

	public void validateViewDeployment(Mapping mapping,Sql sql=getSql()) {
		List<Table> tables=getReverseEngineeredTables(Context.getPhysicalSchemaName(mapping.schema),mapping.name,'VIEW',sql)
		if (!tables ||tables.size()==0) {
			throw new AssertionError("Table [${getSchema()}].[${mapping.getName()}] not deployed in database.")
		} else if (tables.size()>1) {
			throw new AssertionError("Multiple tables matching ${mapping.refference} deployed in database.")
		}
		validateMappingColumns(tables[0],mapping)
	}

	protected void validateTableColumns(Table reversedTable,Table modelTable) {

		String reversedColumns=reversedTable.getColumns().collect({toString(it)}).join(',\n')
		String modelColumns=modelTable.getColumns().collect({toString(it)}).join(',\n')
		Assert.assertEquals("Column definition of deployed ${modelTable.refference} does not match to model.",modelColumns,reversedColumns)
	}

	protected String toString(Column c) {
		"$c.name $c.dataType ${c.description ?: ''}"
	}

	protected void validateMappingColumns(Table reversedTable,Mapping mapping) {
		String reversedColumns=reversedTable.getColumns().collect({"$it.name"}).join(',\n')
		String modelColumns=mapping.getColumns().collect({"$it.alias"}).join(',\n')
		Assert.assertEquals("Column definition of deployed ${mapping.refference} does not match to model.",modelColumns,reversedColumns)
	}
	
	protected List<Table> getReverseEngineeredTables(String tableSchema,String tableName,String tableType,Sql sql) {
		return getReverseEngineeringService().reverseEngineerTables(sql,tableName,tableType,tableSchema)
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (sql!=null) {
			sql.close()
			setSqlProxy(null)
		}
	}
	
	static class SqlProxy {
		Sql sql
		
		SqlProxy() {
			addShutdownHook {close()}
		}
		void close() {
			if (sql!=null) {
				sql.close()
			}
			sql=null
		}
	}

}
