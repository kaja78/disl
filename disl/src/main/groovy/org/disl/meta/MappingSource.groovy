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
package org.disl.meta
import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Test;


abstract class MappingSource extends Base {
	String sourceAlias
	Join join=new Join.NONE(source:this)	

	public abstract String getRefference();
	
	public String toString() {
		return getClass().getSimpleName()
	}
	
	public String getFromClause() {
		return join.fromClause
	}
	
	public MappingSource on(String condition){
		this.join.condition=condition
		this
	}


}
