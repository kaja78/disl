/*
 * Copyright 2015 - 2016 Karel Hübl <karel.huebl@gmail.com>.
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
 * Holds unique key metadata in DISL data model.
 * */
@CompileStatic
class UniqueKeyMeta {
	
	String[] columns=[]

	public static void initUniqueKeys(Table table) {
		UniqueKeys uniqueKeys=table.getClass().getAnnotation(UniqueKeys)
		if (uniqueKeys!=null) {
			uniqueKeys.value().each {UniqueKeyMeta.initUniqueKey(table,(UniqueKey)it)}
		}
		
		UniqueKey uniqueKey=table.getClass().getAnnotation(UniqueKey)
		if (uniqueKey!=null) {
			UniqueKeyMeta.initUniqueKey(table,uniqueKey)
		}
		
		
	}

	private static void initUniqueKey(Table owner,UniqueKey uniqueKey) {		
		owner.uniqueKeys.add(new UniqueKeyMeta(columns: uniqueKey.columns()))
	}
}
