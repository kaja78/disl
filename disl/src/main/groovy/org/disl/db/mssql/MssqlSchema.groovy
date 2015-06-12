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
package org.disl.db.mssql

import groovy.sql.Sql

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLClientInfoException;

import org.disl.meta.PhysicalSchema
import org.junit.Test

class MssqlSchema extends PhysicalSchema {
	String host
	int port=1433
	String databaseName
	String instance
	
	MssqlSchema() {
		jdbcDriver="net.sourceforge.jtds.jdbc.Driver"
	}
	
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

}
