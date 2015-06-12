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

import org.junit.Before
import org.junit.Test
import static groovy.util.GroovyTestCase.*

class TestOperators {

	SqlExpression one
	Table t
	Column A
	Column B
	Mapping m

	@Before
	void init() {
		one=new SqlExpression(expression: {1})
		t=new Table() {
					String getSchema() {
						""
					};
				}
		t.sourceAlias="T"
		A=new Column(name:"A")
		B=new Column(name:"B",parent:t)
		m=new Mapping() {
					ColumnMapping M=e "TO_NUMBER('1')"
					ColumnMapping N=e {"TO_NUMBER('1')"}
					void initMapping(){}
					String getSchema() {
						""
					};
				}
		m.sourceAlias="M"
		m.init()
	}

	@Test
	void testToString() {
		def value="value"
		SqlExpression e=new SqlExpression(expression: {"${value}"})
		assertEquals("value", e.toString())
		value="changedValue"
		assertEquals("changedValue", e.toString())
	}

	@Test
	void testPlus() {
		assertEquals('1+1',(one+one).toString())
		assertEquals('1+1',(one+1).toString())
		assertEquals('1+A',(one+A).toString())
		assertEquals('1+T.B',(one+B).toString())
		assertEquals('A+1+T.B',(A+one+B).toString())
		assertEquals("M.M+A+1+T.B+M.N",(m.M+A+one+B+m.N).toString())
	}
	
	@Test
	void testConcat() {
		assertEquals('1||1',(one.concat(one)).toString())
		assertEquals('1||A||T.B||M.M',(one.concat(A,B,m.M)).toString())
	}
	
	@Test
	void testIsEqual() {	
		assertEquals('1=A',(one.isEqual(A)).toString())		
	}
}
