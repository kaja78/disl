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

class IndexMeta {
	List<Column> columns
	
	public List<String> getColumnNames() {
		return columns.collect({it.name})
	}
	
	public static initIndexes(IndexOwner object) {
		Collection<Index> inds=object.getClass().getAnnotations().findAll {Index.class.equals(it.annotationType())}
		inds.each {initIndex(it,object)}

	}
	
	protected static void initIndex(Index index, IndexOwner object) {
		IndexMeta indexMeta=new IndexMeta(columns: index.columns().collect({object[it]}))
		object.indexes.add(indexMeta)
	}
	
	interface IndexOwner {
		List<IndexMeta> getIndexes();
	}
}