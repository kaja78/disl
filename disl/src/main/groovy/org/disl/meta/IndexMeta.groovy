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
package org.disl.meta;

import java.util.List;
/**
 * Holds index metadata in DISL data model.
 * */
class IndexMeta {
	List<String> columnNames
	
	public static initIndexes(IndexOwner object) {
		Index index=object.getClass().getAnnotation(Index)
		if (index!=null) {
			initIndex(index,object)	
		}

		Indexes indexes=object.getClass().getAnnotation(Indexes)
		if (indexes!=null) {
			indexes.value().each {initIndex(it,object)}
		}
	}
	
	protected static void initIndex(Index index, IndexOwner object) {
		IndexMeta indexMeta=new IndexMeta(columnNames: index.columns())
		object.indexes.add(indexMeta)
	}
}