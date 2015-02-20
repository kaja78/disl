package org.disl.meta;

import groovy.sql.Sql

public class PhysicalSchema {
	String user
	String password
	String jdbcDriver
	String jdbcUrl
	String schema
	
	
	public Sql getSql(){
		Sql.newInstance(getJdbcUrl(), getUser(), getPassword(), getJdbcDriver())
	}
}
