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
package org.disl.db.hsqldb


import java.util.List;
import java.util.Map;

import org.disl.meta.PhysicalSchema;
/**
 * Implementation of Hsqldb PhysicalSchema.
 * */
class HsqldbSchema extends PhysicalSchema {

	String jdbcDriver="org.hsqldb.jdbcDriver"
	
	HsqldbSchema() {
		user="sa"
	}
	
	String getJdbcUrl() {
		"jdbc:hsqldb:${databaseName}"
	}
	
	@Override
	public void init() {
		super.init();
		databaseName=getSchemaProperty("databaseName", "mem:inmemoryDb")		
	}
	
	@Override
	public String evaluateExpressionQuery(String expression) {
		"SELECT ${expression} FROM (VALUES (0))"
	}
	
	@Override
	public String evaluateConditionQuery(String expression) {
		"select 1 from (VALUES (0)) where ${expression}"
	}
	
	@Override
	public String getRecordQuery(int index,String expressions) {
		"select ${index} as DUMMY_KEY,${expressions} from (VALUES(0))\n"
	}

}
