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
 * along with Disl.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.disl.meta

import org.junit.Test

class SqlOperators {
	
	SqlOperators plus(Object o) {
		return new SqlExpression( expression: {"${this}+${o}"})
	}
	
	SqlOperators minus(Object o) {
		return new SqlExpression( expression: {"${this}-${o}"})
	}
	
	SqlOperators multiply(Object o) {
		return new SqlExpression( expression: {"${this}*${o}"})
	}
	
	SqlOperators div(Object o) {
		return new SqlExpression( expression: {"${this}/${o}"})
	}
	
	SqlOperators mod(Object o) {
		return new SqlExpression( expression: {"${this}%${o}"})
	}
	
	SqlOperators or(Object o) {
		return new SqlExpression( expression: {"${this} or ${o}"})
	}
	
	SqlOperators and(Object o) {
		return new SqlExpression( expression: {"${this} and ${o}"})
	}
		
	SqlOperators concat(Object... o) {
		return new SqlExpression( expression: {"${this}||${o.join('||')}"})
	}
	
	SqlOperators IN (Object... o) {
		return new SqlExpression( expression: {"${this} IN (${o.join(',')})"})
	}
	
	SqlOperators isEqual(Object o) {
		return new SqlExpression( expression: {"${this.toString()}=${o.toString()}"})
	}
	
	SqlOperators isNotEqual(Object o) {
		return new SqlExpression( expression: {"${this.toString()}<>${o.toString()}"})
	}
	
	SqlOperators lessThan(Object o) {
		return new SqlExpression( expression: {"${this}<${o}"})
	}
	
	SqlOperators greaterThan(Object o) {
		return new SqlExpression( expression: {"${this}>${o}"})
	}
	
	SqlOperators lessOrEqualThan(Object o) {
		return new SqlExpression( expression: {"${this}<=${o}"})
	}
	
	SqlOperators greaterOrEqualThan(Object o) {
		return new SqlExpression( expression: {"${this}>=${o}"})
	}
	
	SqlOperators between (Object o,Object p) {
		return new SqlExpression( expression: {"${this} between ${o} and ${p}"})
	}
	
		

}
