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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.disl.pattern

import groovy.sql.Sql

import org.disl.meta.Context
import org.disl.meta.Table

abstract class TablePattern extends Pattern {
	Table table

	protected Closure<Sql> getSql() {
		return {Context.getSql(getTable().getSchema())}
	}
	
	@Override
	public String toString() {
		"${this.getClass().getSimpleName()}(${getTable().getName()})"
	}
}
