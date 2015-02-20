package org.disl.meta
import static org.disl.test.AssertSQL.*;
import org.junit.Before
import org.junit.Test


class TestLibrary {

	public static String repeat(expression, times) {
		"REPEAT(${expression},${times})"
	}
	
	@Test
	public void testTrim() {
		assertEquals("'AA'", repeat("'A'",2))
	}
	
}
