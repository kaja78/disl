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
