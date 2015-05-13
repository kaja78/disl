package org.disl.pattern

class ExecuteSQLQueryStep extends ExecuteSQLScriptStep {

	@Override
	protected void executeSqlStatementInternal(String sqlCommand) {
		getSql().executeQuery(sqlCommand)
	}
}
