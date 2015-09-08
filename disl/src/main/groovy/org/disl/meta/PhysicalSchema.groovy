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
package org.disl.meta;

import groovy.sql.Sql

public class PhysicalSchema {
	
	String name
	String user
	String password
	String jdbcDriver
	String jdbcUrl
	String schema
	SqlProxy sqlProxy
	
	public void init() {
		Context context=Context.getContext();
		user=getSchemaProperty("user",user)
		password=getSchemaProperty("password",password)
		jdbcDriver=getSchemaProperty("jdbcDriver",jdbcDriver)
		jdbcUrl=getSchemaProperty("jdbcUrl",jdbcUrl)
		schema=getSchemaProperty("schema",schema)
	}
	
	protected String getSchemaProperty(String key, String defaultValue) {
		Context.getContext().getProperty("${name}.${key}",defaultValue)
	}
	
	protected String getSchemaProperty(String key) {
		Context.getContext().getProperty("${name}.${key}")
	}
	
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
		sql.cacheConnection=true
		sql.cacheStatements=false
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
