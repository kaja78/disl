/*
 * Copyright 2015 - 2016 Karel Hübl <karel.huebl@gmail.com>.
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

/**
 * Column of database table or view.
 * */
class Column extends AbstractSqlExpression {
	String name
	MappingSource parent
	String description
	String dataType
	String defaultValue
	boolean notNull=false
	boolean primaryKey = false

	Column(){}
	
	Column(String name,Table parent) {
		this.name=name
		this.parent=parent
	}
	
	String getColumnDefinition() {
		"${getName()} ${getDataType()}${getDefaultValueClause()}${getConstraint()}"
	}
	
	String getConstraint() {
		if (isNotNull()) {
			return " NOT NULL"
		}
		return ""
	}
	
	String getDefaultValueClause() {
		if (getDefaultValue()==null) {
			return ""
		}
		return " DEFAULT ${getDefaultValue()}"
		
	}
	
	String toString(){
		if (parent==null || parent.getSourceAlias()==null) {
			return name
		}
		"${parent.getSourceAlias()}.${getName()}"
	}
} 