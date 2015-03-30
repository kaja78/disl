package org.disl.reverseEngineering

import groovy.sql.GroovyResultSet
import groovy.sql.GroovyResultSetProxy
import groovy.sql.Sql

import java.sql.ResultSet

import org.disl.meta.Column
import org.disl.meta.Context
import org.disl.meta.Table
import org.junit.Assert;
import org.junit.Test

class ReverseEngineeringService {
	protected static final String SRC_FOLDER="src"
	
	String logicalSchemaName
	ReverseTablePattern reverseTablePattern=new ReverseTablePattern()
	
	public Collection<Table> reverseSchemaTables(String targetPackage,String tablePattern=null,File outputDir=new File(SRC_FOLDER),String[] tableTypes=null,String parentClassName=getAbstractParentTableClassSimpleName(targetPackage)){
		Sql sql=Context.getSql(getLogicalSchemaName())
		String schemaFilterPattern=Context.getContext().getPhysicalSchema(getLogicalSchemaName()).getSchema()
		checkAbstractParentTableExist(targetPackage)
		reverseTablePattern.setPackageName(targetPackage)
		reverseTablePattern.setOutputDir(outputDir)
		reverseTablePattern.setParentClassName(parentClassName)
		ResultSet res=sql.getConnection().getMetaData().getTables(null, schemaFilterPattern, tablePattern, tableTypes)
		GroovyResultSet gRes=new GroovyResultSetProxy(res).getImpl()
		def tables=collectRows(res,{new ReverseEngineeredTable(name: it.TABLE_NAME,description: it.REMARKS, schema:logicalSchemaName)})
		res.close()
		tables.each {
			Table t=it
			res=sql.getConnection().getMetaData().getColumns(null, schemaFilterPattern, t.getName(), null)
			eachRow(res,{t.columns.add(new Column(name: it.COLUMN_NAME,description: it.REMARKS))})
			//TODO: Reverse data types, primary key, foreign keys
			reverseTablePattern.setTable(t)
			reverseTablePattern.execute()
		}
		res.close()		
	}
	
	protected void checkAbstractParentTableExist(String packageName,File outputDir=new File(SRC_FOLDER)) {
		String abstractParentTableFileName=getAbstractParentTableClassSimpleName(packageName)
		String path="${packageName.replace('.','/')}/${abstractParentTableFileName}.groovy"
		File f=new File(outputDir,path)
		if (f.exists()) {
			return
		}
		f.getParentFile().mkdirs()
		f.createNewFile()
		f.write(getAbstractParentTableSourceCode(packageName))		
	}
	
	@Test
	public void TestGetAbstractParentTableSourceCode() {
		String packageName="org.disl.l0"
		String expectedCode="""\
package org.disl.l0

import org.disl.meta.Table

public abstract class AbstractL0Table  extends Table {
		String schema="L0"
}
"""
		logicalSchemaName="L0"
		Assert.assertEquals(expectedCode, getAbstractParentTableSourceCode('org.disl.l0'))
	}
	
	public String getAbstractParentTableSourceCode(String packageName) {
		"""\
package ${packageName}

import org.disl.meta.Table

public abstract class ${getAbstractParentTableClassSimpleName(packageName)}  extends Table {
		String schema="${logicalSchemaName}"
}
"""
	}
	
	@Test
	public void testGetAbstractParentTableFileName() {
		Assert.assertEquals("AbstractL0Table", getAbstractParentTableClassSimpleName("org.disl.l0"))
	}
	
	protected String getAbstractParentTableClassSimpleName(String packageName) {
		String simplePackageName=packageName.substring(packageName.lastIndexOf('.')+1).capitalize()
		"Abstract${simplePackageName}Table"
		
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
