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
package org.disl.db.oracle

import groovy.sql.Sql

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLClientInfoException;

import org.disl.meta.PhysicalSchema
import org.junit.Test

class OracleSchema extends PhysicalSchema {
	String host
	String port=1521
	String databaseName
	
	OracleSchema() {
		jdbcDriver="oracle.jdbc.OracleDriver"
	}
	
	public String getJdbcUrl() {
		"jdbc:oracle:thin:@${getHost()}:${getPort()}${getDatabaseName()}"
	}
	
	public void setSid(String sid) {
		setDatabaseName(":${sid}")
	}
	
	public void setServiceName(String serviceName) {
		setDatabaseName("/${serviceName}")
	}
}
