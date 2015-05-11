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
		Column KEY

		@Description(\"""Natural key\""")
		Column ID

		@Description(\"""Dimension name.\""")
		Column NAME		
}"""
		pattern.execute()
		assertEquals(expectedContent,new File("build/test/l2/TestDimensionTable.groovy").getText())
	}
}
