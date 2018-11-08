/*
 * Copyright 2015 - 2016 Karel H�bl <karel.huebl@gmail.com>.
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
package org.disl.db.oracle

import org.disl.db.reverseEngineering.ReverseEngineeringService;
import org.disl.meta.PhysicalSchema;

/**
 *Oracle physical schema implementation.
 * */
class OracleSchema extends PhysicalSchema {
	String host
	String port=1521
	String jdbcDriver="oracle.jdbc.OracleDriver"

	@Override
	public void init() {
		super.init();
		if (!getJdbcUrl()) {
			host=getSchemaProperty("host", host)
			port=getSchemaProperty("port", port)
			setSid(getSchemaProperty("sid"))
			setServiceName(getSchemaProperty("serviceName"))
			setJdbcUrl("jdbc:oracle:thin:@${getHost()}:${getPort()}${getDatabaseName()}")
		}
	}

	public void setSid(String sid) {
		if (sid!=null) {
			setDatabaseName(":${sid}")
		}
	}

	public void setServiceName(String serviceName) {
		if (serviceName!=null) {
			setDatabaseName("/${serviceName}")
		}
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
	
	@Override
	protected String getValidationQuery(String sqlQuery) {
		"select * from (${sqlQuery}) where 1=2"
	}

	@Override
	protected String getEvaluateRowCountQuery(String sqlQuery) {
		"select count(1) from (${sqlQuery})"
	}

	@Override
	public ReverseEngineeringService getReverseEngineeringService() {
		return new OracleReverseEngineeringService();
	}
}
