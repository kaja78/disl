package org.disl.pattern

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class ExecuteSQLQueryStep extends ExecuteSQLScriptStep {

	@Override
	protected void executeSqlStatementInternal(String sqlCommand) {
		getSql().executeQuery(sqlCommand).close()
	}
}
