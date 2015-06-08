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
package org.disl.reverseEngineering

import groovy.sql.GroovyResultSet
import groovy.sql.GroovyResultSetProxy
import groovy.sql.Sql

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Types

import org.disl.meta.Column
import org.disl.meta.Context
import org.disl.meta.Table

class ReverseEngineeringService {
	protected static final String SRC_FOLDER="src"

	String logicalSchemaName
	ReverseTablePattern reverseTablePattern=new ReverseTablePattern()

	public Collection<Table> reverseSchemaTables(String targetPackage,String tablePattern=null,String sourceSchemaFilterPattern=null,File outputDir=new File(SRC_FOLDER),String[] tableTypes=null,String parentClassName=getAbstractParentTableClassSimpleName(targetPackage)){
		Sql sql=Context.getSql(getLogicalSchemaName())
		if (sourceSchemaFilterPattern==null) {
			sourceSchemaFilterPattern=Context.getContext().getPhysicalSchema(getLogicalSchemaName()).getSchema()
		}
		checkAbstractParentTableExist(targetPackage,outputDir)
		reverseTablePattern.setPackageName(targetPackage)
		reverseTablePattern.setOutputDir(outputDir)
		reverseTablePattern.setParentClassName(parentClassName)
		ResultSet res=sql.getConnection().getMetaData().getTables(null, sourceSchemaFilterPattern, tablePattern, tableTypes)
		GroovyResultSet gRes=new GroovyResultSetProxy(res).getImpl()
		def tables=collectRows(res,{new ReverseEngineeredTable(name: it.TABLE_NAME,description: it.REMARKS, schema:logicalSchemaName)})
		res.close()
		tables.each {
			Table t=it
			res=sql.getConnection().getMetaData().getColumns(null, sourceSchemaFilterPattern, t.getName(), null)
			eachRow(res,{t.columns.add(new Column(name: it.COLUMN_NAME,description: it.REMARKS,dataType: getDataType(it.TYPE_NAME,it.COLUMN_SIZE,it.DECIMAL_DIGITS,it.DATA_TYPE)))})
			//TODO: Reverse data types, primary key, foreign keys
			reverseTablePattern.setTable(t)
			reverseTablePattern.execute()
		}
		res.close()
	}

	protected String getDataType(String dataTypeName,BigDecimal size, BigDecimal decimalDigits,BigDecimal sqlDataType) {
		if (size==null || isIgnoreSize(sqlDataType.intValue())) {
			return "${dataTypeName}"
		}
		if (decimalDigits==null) {
			return "${dataTypeName}(${size})"
		}
		return "${dataTypeName}(${size},${decimalDigits})"
	}
	boolean isIgnoreSize(int sqlType) {
		List ignoredTypes=[Types.DATE, Types.TIME, Types.ROWID]
		return ignoredTypes.contains(sqlType)
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


	public String getAbstractParentTableSourceCode(String packageName) {
		"""\
package ${packageName}

import org.disl.meta.Table

public abstract class ${getAbstractParentTableClassSimpleName(packageName)}  extends Table {
		String schema="${logicalSchemaName}"
}
"""
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

	public void traceColumnMappings(Sql sql,String query) {
		String selectList=query.substring(query.toUpperCase().indexOf("SELECT")+6,query.toUpperCase().indexOf("FROM"))
		List columnExpressions=selectList.split(',').collect ({
			if (it.contains(" as ")) {
				return it.substring(0, it.lastIndexOf(" as ")).trim()
			}
			return it.trim()
		})
		PreparedStatement stmt=sql.getConnection().prepareStatement(query)
		ResultSetMetaData metadata=stmt.executeQuery().getMetaData()


		for (int i=1;i<=metadata.columnCount;i++) {
			println """ColumnMapping ${metadata.getColumnLabel(i)}=e {"${columnExpressions[i-1]}"}"""
		}
	}
}
