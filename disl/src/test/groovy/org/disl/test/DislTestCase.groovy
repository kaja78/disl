package org.disl.test

import groovy.sql.Sql

import org.disl.meta.Context

abstract class DislTestCase extends AbstractDislTestCase {
	
	DislTestCase() {
		Context.setContextName("disl-test")
	}
	
	protected Sql createSql() {		
		def sql=Context.getSql("L2")
		sql.execute("CREATE TABLE DUAL (dummy char(1))")
		sql.execute("INSERT INTO DUAL VALUES ('X')")
		return sql		
	}
}
