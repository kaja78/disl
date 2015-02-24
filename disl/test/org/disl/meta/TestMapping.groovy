package org.disl.meta

import static org.disl.meta.TestLibrary.*
import static org.junit.Assert.*

import org.junit.Test

class TestMapping extends Mapping {
	
	TestTable s1
	TestTable s2
	
	ColumnMapping A=e {"$s1.A"} 
	ColumnMapping c=e "C"
	ColumnMapping B=a {repeat(s2.B,3)}
	
	
	void initMapping() {
		from s1
		innerJoin s2 on "$s1.A=$s2.A"
		
		where "$s1.A=$s1.A"
		
		groupBy "$s1.A,C"		
	}
	
	@Test
	void testGetSQLQuery() {
		TestMapping m=MetaFactory.create(TestMapping)
		assertEquals ("""	/*Mapping TestMapping*/
		SELECT
			s1.A as A,
			C as c,
			REPEAT(s2.B,3) as B
		FROM
			TestTable s1,
			INNER JOIN TestTable s2  ON (s1.A=s2.A)
		WHERE
			s1.A=s1.A
		GROUP BY
			s1.A,C
	/*End of mapping TestMapping*/
""".toString(),m.getSQLQuery())
	}
}
