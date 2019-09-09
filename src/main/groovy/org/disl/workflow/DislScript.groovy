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
package org.disl.workflow

import groovy.sql.Sql
import groovy.transform.CompileStatic

import org.disl.meta.Context
import org.disl.meta.Mapping
import org.disl.meta.MetaFactory
import org.disl.pattern.Executable
import org.disl.pattern.ExecuteSQLScriptStep
import org.disl.pattern.ExecutionInfo
import org.disl.util.sqlDep.CreateSqlSetRequest
import org.disl.util.sqlDep.SqlDepService

/**
 * DISL script class.
 *
 * Sample DISL script:
 * {@code
 @BaseScript(org.disl.meta.DislScript)
 import groovy.transform.BaseScript
 
 execute [YOUR_DISL_MODEL_CLASS]
 }
 * */
@CompileStatic
abstract class DislScript extends Script implements Executable{
	
	String schema='default'
	boolean exitOnFinish=true
	ExecutionInfo executionInfo=new ExecutionInfo()

	abstract def runScript()

	void setExitOnFinish(boolean  exitOnFinish) {
		this.exitOnFinish=exitOnFinish
	}

	def run() {
		runScript()
		if (exitOnFinish) {
			System.exit(0)
		}
	}

	@Override
	void execute(){
		executionInfo.start()
		try {
			this.run()
			executionInfo.finish()
		} catch (Exception e) {
			executionInfo.error(e)
			throw e
		}
	}
	
	@Override
	void simulate(){
		println "Simulating execution of script ${this.getClass().getName()}"		
	}
	
	void execute(Class<Executable> executable) {
		create(executable).execute()
	}

	void execute(Collection<Class<Executable>> executable) {
		create(executable).each {it.execute()}
	}
	
	/**
	 * Execute SQL script.
	 * */
	void executeSql(String code,boolean ignoreErrors=false,String commandSeparator=';') {
		new SqlScriptExecutor(ignoreErrors: ignoreErrors,commandSeparator: commandSeparator,code: code,schema: schema).execute()
	}
	
	void simulate(Class<Executable> executable) {
		create(executable).simulate()
	}

	void simulate(Collection<Class<Executable>> executable) {
		create(executable).each {it.simulate()}
	}

	void traceQuerySQL(Class<Mapping> mapping) {
		println create(mapping).getSQLQuery()
	}

	/**
	 * Trace first 100 rows of Mapping SQL query
	 * */
	void traceQueryData(Class<Mapping> mapping) {
		println create(mapping).exportToCSV(100)
	}

	void validate(Class<Mapping> mapping) {
		create(mapping).validate()
	}

	void validate(Collection<Class<Mapping>> mappings) {
		create(mappings).each {it.validate()}
	}

	public <T> T create(Class<T> executable) {
		MetaFactory.create(executable)
	}

	public <T> Collection<T> create(Collection<Class<T>> executable) {
		executable.collect {MetaFactory.create(it)}
	}
	
	public <T> Collection<T> createAll(String rootPackage,Class assignableType) {
		MetaFactory.createAll(rootPackage, assignableType)
	}
	
	public void sqlDepPublish(String sqlDepUserAccountId,String sqlDepCustomSqlSetName,Collection objects) {
		CreateSqlSetRequest request=new CreateSqlSetRequest(userAccountId: sqlDepUserAccountId,customSqlSetName:sqlDepCustomSqlSetName)
		request.add(objects.toArray())
		println new SqlDepService().post(request)		
	}

	Context getContext() {
		Context.getContext()
	}

	void setContextName(String name) {
		Context.setContextName(name)
	}
	
	protected static class SqlScriptExecutor extends ExecuteSQLScriptStep {
		String code
		String schema
		
		Sql getSql() {
			Context.getContext().getSql(schema)
		}
	}
	
	public static String encode(String value) {
		return value.getBytes().encodeBase64()
	}
	
	public static String decode(String value) {
		return new String(value.decodeBase64())
	}
	
}
