package org.disl.reverseEngineering

import groovy.sql.GroovyResultSet
import groovy.sql.GroovyResultSetProxy
import groovy.sql.Sql

import java.sql.ResultSet

import org.disl.meta.Column
import org.disl.meta.Context
import org.disl.meta.Table
import org.junit.Test

class ReverseEngineeringService {
	String logicalSchemaName
	ReverseTablePattern reverseTablePattern=new ReverseTablePattern()
	
	public Collection<Table> reverseSchemaTables(String targetPackage,String tablePattern=null,File outputDir=new File("src"),String[] tableTypes=null){
		Sql sql=Context.getSql(getLogicalSchemaName())
		String schemaPattern=Context.getContext().getPhysicalSchema(getLogicalSchemaName()).getSchema()
		reverseTablePattern.setPackageName(targetPackage)
		reverseTablePattern.setOutputDir(outputDir)
		ResultSet res=sql.getConnection().getMetaData().getTables(null, schemaPattern, tablePattern, tableTypes)
		GroovyResultSet gRes=new GroovyResultSetProxy(res).getImpl()
		def tables=collectRows(res,{new ReverseEngineeredTable(name: it.TABLE_NAME,description: it.REMARKS, schema:logicalSchemaName)})
		res.close()
		tables.each {
			Table t=it
			res=sql.getConnection().getMetaData().getColumns(null, schemaPattern, t.getName(), null)
			eachRow(res,{t.columns.add(new Column(name: it.COLUMN_NAME,description: it.REMARKS))})
			//TODO: Reverse data types, primary key, foreign keys
			reverseTablePattern.setTable(t)
			reverseTablePattern.execute()
		}
		res.close()		
	}
	
	protected Collection collectRows(ResultSet res,Closure collectClosure) {
		def resultList=[]
		eachRow(res,{resultList.add(collectClosure(it))}) 		
		resultList	
	}
	
	protected void eachRow(ResultSet res,Closure eachRowClosure) {
		GroovyResultSet gRes=new GroovyResultSetProxy(res).getImpl()
		gRes.eachRow eachRowClosure
	}
	
	private static class ReverseEngineeredTable extends Table {
		String schema
	}
}
