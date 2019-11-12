/*
 * Copyright 2015 - 2016 Karel H�bl <karel.huebl@gmail.com>.
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
package org.disl.meta

import groovy.transform.CompileStatic;

/**
 * ColumnMapping defines data transformation for single column in Mapping.
 * */
@CompileStatic
abstract class ColumnMapping extends AbstractSqlExpression {

	Mapping parent
	
	/**
	 * Sql expression defining data transformation.
	 * */
	String expression
	
	/**
	 * Name of column in ResultSet.
	 * */
	String alias
	
	String description
	
	String toString(){
		if (parent.getSourceAlias()==null) {
			return getAlias();
		}
		"${parent.getSourceAlias()}.${getAlias()}"
	}
	
	String getAliasedMappingExpression() {
		"$expression as $alias"
	}
	
}