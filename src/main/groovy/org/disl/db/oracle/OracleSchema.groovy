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
package org.disl.db.oracle

import org.disl.db.reverseEngineering.ReverseEngineeringService
import org.disl.meta.Mapping;
import org.disl.meta.PhysicalSchema

import java.sql.SQLException;

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
		"select ${index} as DUMMY_KEY,${expressions} from dual"
	}

	public void validateQuery(Mapping mapping) throws AssertionError {
		try {
			getSql().execute(getValidationQuery(mapping))
		} catch (SQLException e) {
			throw new AssertionError("Validation failed with message: ${e.getMessage()} for query:\n${getValidationQuery(mapping)}")
		}
	}

	@Override
	protected String getValidationQuery(Mapping mapping) {
		"""\
declare
${OracleSchema.getBindVariablesDeclaration(mapping)} 
\tcursor c is ${mapping.getSQLQuery()}; 
begin 
\tnull; 
end;"""
	}

	public static String getBindVariablesDeclaration(Mapping mapping) {
		mapping.getBindVariables().collect({"""\
\t${it.name} ${it.dataType}:=${it.value};
"""}).join()
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
