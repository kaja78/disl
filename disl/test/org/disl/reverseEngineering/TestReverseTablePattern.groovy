package org.disl.reverseEngineering

import org.disl.meta.MetaFactory
import org.disl.meta.TestDimensionTable
import org.junit.Test

class TestReverseTablePattern {

	def pattern=new ReverseTablePattern(
		table: MetaFactory.create(TestDimensionTable),
		outputDir: new File("build/test"),
		packageName: "l2")

	@Test
	public void testSimulate() {
		pattern.simulate()
	}

	@Test
	public void testExecute() {
		pattern.execute()
		assert new File("build/test/l2/TestDimensionTable.groovy").getText()=="""\
package l2

import org.disl.meta.*

@Description(\"""null\""")
class TestDimensionTable {
		@Description(\"""Surrogate key.\""")
		Column KEY

		@Description(\"""Natural key\""")
		Column ID

		@Description(\"""Dimension name.\""")
		Column NAME		
}"""
	}
}
