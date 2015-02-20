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
	class LEFT{
		public String getFromClause() {

		}
	}
	class RIGHT{
		public String getFromClause() {

		}
	}
	class FULL{
		public String getFromClause() {

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
