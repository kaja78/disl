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
		if (sql==null || (sql.connection!=null && sql.connection.isClosed())) {
			sql=createSql()	
		}
		sql
	}

	protected createSql() {
		def sql=Sql.newInstance(getJdbcUrl(), getUser(), getPassword(), getJdbcDriver())
		sql.getConnection().setAutoCommit(false)
		sql
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (sql!=null) {
			sql.close()
			sql=null
		}
	}

}
