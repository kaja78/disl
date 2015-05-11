package org.disl.meta
import groovy.sql.Sql


class Schema {
	String name

	String jdbcDriver
	String url
	String userName
	String password


	public Sql getSql() {
		Class.forName(jdbcDriver)
		Sql.newInstance(url,userName,password)
	}
}
