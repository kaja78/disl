package org.disl.meta

public abstract class SetOperation {
	
	Mapping source
	
	public abstract String getSetOperationClause();
	
	static class UNION extends SetOperation {
		@Override
		public String getSetOperationClause() {
			"UNION ${source.getSQLQuery()}"
		}
	}
	static class UNION_ALL extends SetOperation {
		@Override
		public String getSetOperationClause() {
			"UNION ALL ${source.getSQLQuery()}"
		}
	}
	static class INTERSECT extends SetOperation {
		@Override
		public String getSetOperationClause() {
			"INTERSECT ${source.getSQLQuery()}"
		}
	}
	static class MINUS extends SetOperation {
		@Override
		public String getSetOperationClause() {
			"MINUS ${source.getSQLQuery()}"
		}
	}


}
