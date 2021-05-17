/*
 *
 * Copyright 2015 - 2021 Karel Hübl <karel.huebl@gmail.com>.
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
 * Interface for Mappings defining load of target table.
 * The mapping column names are expected to exactly match target table column names.
 * */
@CompileStatic
interface TableMapping<T extends Table> {
	
	T getTarget()
	List<ColumnMapping> getColumns()
	String getInitialMapping(String columnName)
	
	default void checkUnmappedTargetColumns() {
		if (getUnmappedTargetColumns().size()>0) {
			traceUnmappedColumns()
			throw new AssertionError("Found unmapped columns: ${getUnmappedTargetColumns().collect({it.name}).join(',')}",null)
		}
	}

	default void checkColumnsMissingInTarget() {
		if (getColumnsMissingInTarget().size()>0) {
			throw new AssertionError("Found columns missing in target: ${getColumnsMissingInTarget().join(',')}",null)
		}
	}

	default void traceUnmappedColumns() {
		getUnmappedTargetColumns().each {println getInitialMapping(it.name)}
	}

	default Collection<Column> getUnmappedTargetColumns() {
		return getTarget().getColumns().findAll {
			Column targetColumn=it
			boolean unmapped=true
			columns.each {
				if (it.getAlias().equals(targetColumn.name)) {
					unmapped=false
					return
				}
			}
			return unmapped
		}
	}

	default Collection<ColumnMapping> getColumnsMissingInTarget() {
		return getColumns().findAll {
			ColumnMapping columnMapping=it
			boolean missing=true
			target.columns.each {
				if (it.name.equals(columnMapping.alias)) {
					missing=false
				}
			}
			return missing
		}
	}

}
