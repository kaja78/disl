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

import groovy.transform.CompileStatic

/**
 * Data source of data transformation defined in Mapping.
 * */
@CompileStatic
abstract class MappingSource extends Base {
	String sourceAlias
	Join join=new Join.NONE(source:this)	

	public abstract String getRefference();
	
	public abstract String getRefferenceColumnList();
	
	public abstract String getSchema()
	
	public String toString() {
		return getClass().getSimpleName()
	}
	
	public String getFromClause() {
		return join.fromClause
	}
	
	/**
	 * Define join condition based SQL expression.
	 * */
	public MappingSource on(Object condition){
		this.join.condition=condition.toString()
		this
	}
	
	/**
	 * Shorthand method for on("${expression1}=${expression2}").
	 * */
	public MappingSource on(Object expression1,Object expression2) {
		return on("${expression1}=${expression2}")
	}


}
