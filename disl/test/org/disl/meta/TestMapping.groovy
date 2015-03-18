package org.disl.meta

import static org.disl.meta.TestLibrary.*
import static org.junit.Assert.*

import org.disl.meta.Join.CARTESIAN;
import org.disl.meta.Join.RIGHT;
import org.junit.Test

class TestMapping extends Mapping {
	
	TestTable s1
	TestTable s2
	TestTable s3
	TestTable s4
	TestTable s5
	TestTable s6
	
	ColumnMapping A=e {"$s1.A"} 
	ColumnMapping c=e "C"
	ColumnMapping B=a {repeat(s2.B,3)}
	
	
	void initMapping() {
		from s1
		innerJoin s2 on "$s1.A=$s2.A"
		leftOuterJoin s3 on "$s2.A=$s3.A"
		rightOuterJoin s4 on "$s2.A=$s4.A"
		fullOuterJoin s5 on "$s2.A=$s5.A"
		cartesianJoin s6
		
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
			TestTable s1
			INNER JOIN TestTable s2  ON (s1.A=s2.A)
			LEFT OUTER JOIN TestTable s3  ON (s2.A=s3.A)
			RIGHT OUTER JOIN TestTable s4  ON (s2.A=s4.A)
			FULL OUTER JOIN TestTable s5  ON (s2.A=s5.A)
			,TestTable s6
		WHERE
			s1.A=s1.A
		GROUP BY
			s1.A,C
	/*End of mapping TestMapping*/""".toString(),m.getSQLQuery())
	}
}
