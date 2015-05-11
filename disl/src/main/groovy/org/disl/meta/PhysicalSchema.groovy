package org.disl.meta;

import groovy.sql.Sql

public class PhysicalSchema {
	String user
	String password
	String jdbcDriver
	String jdbcUrl
	String schema
	Sql sql
	
	public Sql getSql(){
		if (sql==null) {
			sql=createSql()						
		}
		sql
	}

	protected createSql() {
		def sql=Sql.newInstance(getJdbcUrl(), getUser(), getPassword(), getJdbcDriver())
		sql.getConnection().setAutoCommit(false)
		sql
	}

}
