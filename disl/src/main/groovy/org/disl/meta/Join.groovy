/*
 * Copyright 2015 Karel Hübl <karel.huebl@gmail.com>.
 *
 * This file is part of disl.
 *
 * Disl is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Disl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.disl.meta;

public abstract class Join {

	MappingSource source
	String condition
	
	public abstract String getFromClause();

	public static class NONE extends Join {
		public String getFromClause()  {
			"$source.refference"
		}
	}
	public static class INNER extends Join {
		public String getFromClause() {
			"INNER JOIN $source.refference  ON ($condition)"
		}
	}
	public static class LEFT extends Join {
		public String getFromClause() {
			"LEFT OUTER JOIN $source.refference  ON ($condition)"
		}
	}
	public static class RIGHT extends Join{
		public String getFromClause() {
			"RIGHT OUTER JOIN $source.refference  ON ($condition)"
		}
	}
	public static class FULL extends Join{
		public String getFromClause() {
			"FULL OUTER JOIN $source.refference  ON ($condition)"
		}
	}
	
	public static class CARTESIAN extends Join{
		public String getFromClause() {
			",$source.refference"
		}
	}
}
