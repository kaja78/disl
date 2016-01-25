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
package org.disl.db.vertica

import org.disl.meta.PhysicalSchema

/**
 * Vertica analytic platform PhysicalSchema implementation.
 * */
class VerticaSchema extends PhysicalSchema {
	
	String host
	int port=5433
	String databaseName
	String jdbcDriver="com.vertica.Driver"
	
	@Override
	public String getJdbcUrl() {
		"jdbc:vertica://${host}:${port}/${databaseName}"
	}
	
	@Override
	public String evaluateExpressionQuery(String expression) {
		"SELECT ${expression} FROM dual"
	}
	
	@Override
	public String evaluateConditionQuery(String expression) {
		"select 1 from dual where ${expression}"
	}
	
	@Override
	public String getRecordQuery(int index,String expressions) {
		"select ${index} as DUMMY_KEY,${expressions} from dual\n"
	}
}
