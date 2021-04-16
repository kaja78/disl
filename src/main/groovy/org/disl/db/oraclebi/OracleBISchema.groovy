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
package org.disl.db.oraclebi

import org.disl.meta.PhysicalSchema;

/**
 * Oracle BI server PhysicalSchema implementation.
 * */
class OracleBISchema extends PhysicalSchema {
	String host
	String port=9703
	String jdbcDriver="oracle.bi.jdbc.AnaJdbcDriver"

	
	@Override
	public void init() {
		super.init();
		if (!getJdbcUrl()) {
			host=getSchemaProperty("host", host)
			port=getSchemaProperty("port", port)
			setJdbcUrl("jdbc:oraclebi://${getHost()}:${getPort()}/")
		}
	}

	@Override
	public String evaluateExpressionQuery(String expression) {
		throw new UnsupportedOperationException()
	}
	
	@Override
	public String evaluateConditionQuery(String expression) {
		throw new UnsupportedOperationException()
	}
	
	@Override
	public String getRecordQuery(int index,String expressions) {
		throw new UnsupportedOperationException()
	}
	
}
