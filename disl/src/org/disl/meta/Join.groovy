package org.disl.meta;

public abstract class Join {

	MappingSource source
	String condition
	
	public abstract String getFromClause();

	public static class NONE extends Join {
		public String getFromClause()  {
			if (source.sourceAlias!=null) {
				return "$source.name $source.sourceAlias"
			}
			return "$source.name"
		}
	}
	public static class INNER extends Join {
		public String getFromClause() {
			"INNER JOIN $source.name $source.sourceAlias  ON ($condition)"
		}
	}
	public static class LEFT extends Join {
		public String getFromClause() {
			"LEFT OUTER JOIN $source.name $source.sourceAlias  ON ($condition)"
		}
	}
	public static class RIGHT extends Join{
		public String getFromClause() {
			"RIGHT OUTER JOIN $source.name $source.sourceAlias  ON ($condition)"
		}
	}
	public static class FULL extends Join{
		public String getFromClause() {
			"FULL OUTER JOIN $source.name $source.sourceAlias  ON ($condition)"
		}
	}
	
	public static class CARTESIAN extends NONE{
		public String getFromClause() {
			",${super.getFromClause()}"
		}
	}
	class UNION{
		public String getFromClause() {

		}
	}
	class UNION_ALL{
		public String getFromClause() {

		}
	}
	class INTERSECT{
		public String getFromClause() {

		}
	}
	class MINUS{
		public String getFromClause() {

		}
	}
}
