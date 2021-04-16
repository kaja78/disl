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
package org.disl.meta;

import groovy.transform.CompileStatic

/**
 * Holds index metadata in DISL data model.
 * */
@CompileStatic
class IndexMeta {
	List<String> columnNames
	
	public static initIndexes(IndexOwner object) {
		Index index=object.getClass().getAnnotation(Index)
		if (index!=null) {
			initIndex(index,object)	
		}

		Indexes indexes=object.getClass().getAnnotation(Indexes)
		if (indexes!=null) {
			indexes.value().each {initIndex((Index)it,object)}
		}
	}
	
	protected static void initIndex(Index index, IndexOwner object) {
		IndexMeta indexMeta=new IndexMeta(columnNames: Arrays.asList(index.columns()))
		object.indexes.add(indexMeta)
	} 
}