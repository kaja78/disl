package org.disl.meta

import org.hsqldb.jdbcDriver;

import groovy.sql.Sql

@Singleton
class TestingDatabaseProvider {
	def Sql sql=createSql()
	
	private Sql createSql() {
		Class.forName( jdbcDriver.getName())
		Sql sql=Sql.newInstance("jdbc:hsqldb:mem:testingDb", "sa", "");
		sql.execute("create table DUAL (DUMMY CHAR(1))")
		sql.execute("insert into DUAL VALUES('X')")
		sql
	}
}
