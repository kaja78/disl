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
package org.disl.reverseEngineering

import org.disl.meta.MetaFactory
import org.disl.meta.TestDimensionTable
import org.junit.Test
import static org.junit.Assert.*

class TestReverseTablePattern {

	def pattern=new ReverseTablePattern(
	table: MetaFactory.create(TestDimensionTable),
	outputDir: new File("build/test"),
	parentClassName: "AbstractL2Table", 
	packageName: "l2")

	@Test
	public void testSimulate() {
		pattern.simulate()
	}

	@Test
	public void testExecute() {
		String expectedContent="""\
package l2

import org.disl.meta.*

@Description(\"""This is testing dimension.\""")
class TestDimensionTable extends AbstractL2Table {

		@Description(\"""Surrogate key.\""")
		@DataType("INTEGER")
		Column KEY

		@Description(\"""Natural key\""")
		@DataType("INTEGER")
		Column ID

		@Description(\"""Dimension name.\""")
		@DataType("VARCHAR(200)")
		Column NAME		
}"""
		pattern.execute()
		assertEquals(expectedContent,new File("build/test/l2/TestDimensionTable.groovy").getText())
	}
}
