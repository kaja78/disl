package org.disl.meta;

import groovy.sql.Sql

public class PhysicalSchema {
	String user
	String password
	String jdbcDriver
	String jdbcUrl
	String schema
	SqlProxy sqlProxy
	
	public Sql getSql(){
		if (sqlProxy==null || (sqlProxy.sql.connection==null || sqlProxy.sql.connection.isClosed())) {
			sqlProxy=createSqlProxy()	
		}
		sqlProxy.sql
	}

	protected SqlProxy createSqlProxy() {
		def sql=createSql()
		return new SqlProxy(sql: sql)
	}
	
	protected Sql createSql() {
		def sql=Sql.newInstance(getJdbcUrl(), getUser(), getPassword(), getJdbcDriver())
		sql.getConnection().setAutoCommit(false)
		return sql
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (sql!=null) {
			sql.close()
			sql=null
		}
	}
	
	static class SqlProxy {
		Sql sql
		
		SqlProxy() {
			addShutdownHook {close()}
		}
		void close() {
			if (sql!=null) {
				sql.close()
			}
			sql=null
		}
	}

}
