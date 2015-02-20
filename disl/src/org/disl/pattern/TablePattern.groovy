package org.disl.pattern

import groovy.sql.Sql

import org.disl.meta.Context
import org.disl.meta.Table

abstract class TablePattern extends Pattern {
	Table table
	
	protected Sql sql
	
	protected Sql getSql() {
		if (sql==null) {
			sql=Context.get().getSql(getTable().getSchema())
		}
		sql
	}
}
