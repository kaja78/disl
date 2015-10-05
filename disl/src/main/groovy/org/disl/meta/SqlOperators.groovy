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

/**
 * Abstract implementation of basic SQLExpression operators.
 * */
abstract class AbstractSqlExpression {
	
	AbstractSqlExpression plus(Object o) {
		return new SqlExpression( expression: {"${this}+${o}"})
	}
	
	AbstractSqlExpression minus(Object o) {
		return new SqlExpression( expression: {"${this}-${o}"})
	}
	
	AbstractSqlExpression multiply(Object o) {
		return new SqlExpression( expression: {"${this}*${o}"})
	}
	
	AbstractSqlExpression div(Object o) {
		return new SqlExpression( expression: {"${this}/${o}"})
	}
	
	AbstractSqlExpression mod(Object o) {
		return new SqlExpression( expression: {"${this}%${o}"})
	}
	
	AbstractSqlExpression or(Object o) {
		return new SqlExpression( expression: {"${this} or ${o}"})
	}
	
	AbstractSqlExpression and(Object o) {
		return new SqlExpression( expression: {"${this} and ${o}"})
	}
		
	AbstractSqlExpression concat(Object... o) {
		return new SqlExpression( expression: {"${this}||${o.join('||')}"})
	}
	
	AbstractSqlExpression IN (Object... o) {
		return new SqlExpression( expression: {"${this} IN (${o.join(',')})"})
	}
	
	AbstractSqlExpression isEqual(Object o) {
		return new SqlExpression( expression: {"${this.toString()}=${o.toString()}"})
	}
	
	AbstractSqlExpression isNotEqual(Object o) {
		return new SqlExpression( expression: {"${this.toString()}<>${o.toString()}"})
	}
	
	AbstractSqlExpression lessThan(Object o) {
		return new SqlExpression( expression: {"${this}<${o}"})
	}
	
	AbstractSqlExpression greaterThan(Object o) {
		return new SqlExpression( expression: {"${this}>${o}"})
	}
	
	AbstractSqlExpression lessOrEqualThan(Object o) {
		return new SqlExpression( expression: {"${this}<=${o}"})
	}
	
	AbstractSqlExpression greaterOrEqualThan(Object o) {
		return new SqlExpression( expression: {"${this}>=${o}"})
	}
	
	AbstractSqlExpression between (Object o,Object p) {
		return new SqlExpression( expression: {"${this} between ${o} and ${p}"})
	}
	
		

}
