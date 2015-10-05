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

/**
 * Defines logical column. 
 * Logical columns are used in data source (L0) model to enable to extract data from database functions or expression, which are only valid in data source database.
 * Logical columns should be used  with caution, because its usage hides data tranformation logic. 
 * */
class FunctionalColumn extends Column {
	String defaultMapping

	String getMapping(){
		if (defaultMapping!=null) {
			return defaultMapping+' '+toString()
		}
		toString()
	}
}
