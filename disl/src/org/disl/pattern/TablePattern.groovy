package org.disl.pattern

import groovy.sql.Sql

import net.sourceforge.jtds.jdbc.SQLParser;

import org.disl.meta.Context
import org.disl.meta.Table

abstract class TablePattern extends Pattern {
	Table table

		protected Closure<Sql> getSql() {
			return {Context.getSql(getTable().getSchema())}
		}
}
