package org.disl.pattern

import groovy.sql.Sql

import org.disl.meta.Context
import org.disl.meta.TableMapping

abstract class MappingPattern extends Pattern {
	TableMapping mapping

	protected Closure<Sql> getSql() {
		return {Context.getSql(getMapping().getSchema())}
	}
	
	@Override
	public String toString() {
		"${this.getClass().getSimpleName()}(${getMapping().getName()})}"
	}
}
