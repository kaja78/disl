package org.disl.meta

import org.disl.test.DislTestCase
import org.junit.Test


class TestLibrary extends DislTestCase {

	public static String repeat(expression, times) {
		"REPEAT(${expression},${times})"
	}
	
	@Test
	public void testTrim() {
		assertExpressionEquals("'AA'", repeat("'A'",2))
	}
	
}
