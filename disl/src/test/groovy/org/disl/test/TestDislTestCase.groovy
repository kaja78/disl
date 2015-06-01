package org.disl.test

import org.junit.Test

class TestDislTestCase extends DislTestCase {
	
	@Test
	public void testAssertExpressionTrue() {
		assertExpressionTrue("1=1")
	}
	
	@Test
	public void testAssertRowCount() {
		assertRowCount(1, "select 1 from dual")
	}
	
	@Test
	public void testAssertExpressionEquals() {
		assertExpressionEquals("1+1","2")
	}
	
	@Test
	public void testEvaluate() {
		assert "2"==evaluate("1+1")
		assert "11"==evaluate("sum(A)",[["A":6], ["A":5]])
		assert "11"==evaluate("sum(A)",[["A":6,"B.B":1], ["A":5,"B.B":1]])
		assert "2"==evaluate("sum(B.B)",[["A.A":6,"B.B":1], ["A.A":5,"B.B":1]])
	}
	
	@Test
	public void testMapToQuery() {
		assert "select 1 as DUMMY_KEY,1 as A,2 as B from dual\n"==mapToQuery(["A.A":1,"B":2], "A",1, true)
		assert "select 2 as DUMMY_KEY,1 as A from dual\n"==mapToQuery(["A.A":1,"B":2], "A", 2,false)
	}


	@Test
	public void testMapToSubQuery() {
		assertEquals '''\
(select 1 as DUMMY_KEY,1 as a,2 as b from dual
union all
select 2 as DUMMY_KEY,2 as a,4 as b from dual
) SRC
where
1=1
AND SRC.DUMMY_KEY=SRC.DUMMY_KEY''',recordsToSubquery([["a":1,"b":2], ["a":2,"b":4]])
		assertEquals '''\
(select 1 as DUMMY_KEY,1 as a from dual
union all
select 2 as DUMMY_KEY,2 as a from dual
) A,
(select 1 as DUMMY_KEY,2 as b from dual
union all
select 2 as DUMMY_KEY,4 as b from dual
) B
where
1=1
AND A.DUMMY_KEY=A.DUMMY_KEY
AND B.DUMMY_KEY=A.DUMMY_KEY''',recordsToSubquery([["A.a":1,"B.b":2], ["A.a":2,"B.b":4]])
	}

}
