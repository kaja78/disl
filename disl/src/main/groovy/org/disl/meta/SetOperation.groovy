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
 * along with Disl.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.disl.meta

import groovy.transform.CompileStatic

/**
 * Defines set operation to be performed on Mapping's sources.
 * */
@CompileStatic
public abstract class SetOperation {
	
	MappingSource source
	
	public abstract String getSetOperationClause();
	
	static class UNION extends SetOperation {
		@Override
		public String getSetOperationClause() {
			"UNION select * from ${source.getRefference()}"
		}
	}
	static class UNION_ALL extends SetOperation {
		@Override
		public String getSetOperationClause() {
			"UNION ALL select * from ${source.getRefference()}"
		}
	}
	static class INTERSECT extends SetOperation {
		@Override
		public String getSetOperationClause() {
			"INTERSECT select * from ${source.getRefference()}"
		}
	}
	static class MINUS extends SetOperation {
		@Override
		public String getSetOperationClause() {
			"MINUS select * from ${source.getRefference()}"
		}
	}


}
