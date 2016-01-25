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
package org.disl.db.mssql

import groovy.sql.Sql;

import org.disl.meta.PhysicalSchema;
/**
 * Implementation of MS SQL Server PhysicalSchema based on JTDS JDBC driver (groupId: net.sourceforge.jtds, artifactId: jtds). 
 * */
class MssqlSchema extends PhysicalSchema {
	String host
	int port=1433
	String databaseName
	String instance

	String jdbcDriver="net.sourceforge.jtds.jdbc.Driver"

	public String getJdbcUrl() {
		"jdbc:jtds:sqlserver://${getHost()}:${getPort()}/${getDatabaseName()};instance=${getInstance()};user=${getUser()};password=${getPassword()};"
	}

	@Override
	protected Sql createSql() {
		//Pass username & password to jdbcUrl to prevent SSO error when ntlmauth.dll is not on path.
		def sql=Sql.newInstance(getJdbcUrl(),getJdbcDriver());
		sql.getConnection().setAutoCommit(false)
		sql
	}
	
	@Override
	public String evaluateExpressionQuery(String expression) {
		"SELECT ${expression}"
	}
	
	@Override
	public String evaluateConditionQuery(String expression) {
		"select 1 where ${expression}"
	}
	
	@Override
	public String getRecordQuery(int index,String expressions) {
		"select ${index} as DUMMY_KEY,${expressions}\n"
	}
}
