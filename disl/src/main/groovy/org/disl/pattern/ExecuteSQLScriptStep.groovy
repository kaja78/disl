package org.disl.pattern;

import static org.junit.Assert.*
import groovy.sql.Sql

import org.disl.meta.Base
import org.junit.Assert
import org.junit.Test

public class ExecuteSQLScriptStep extends Step {

	public static final String BACKSLASH_NEW_LINE = "\\\\\n"
	
	boolean ignoreErrors=false;
	String commandSeparator=";";
	int updatedRowCount;
	def sql;

	Sql getSql() {
		if (sql instanceof Closure) {
			return sql.call();
		}
		sql
	}

	@Override
	public void execute() {
		try {
		getCommands().each {
			executeSqlStatement(it)
		}
		getSql().commit()
		} catch (Exception e) {
			getSql().rollback()
			throw e
		}
	}
	
	protected Collection<String> getCommands() {
		return code.split(getCommandSeparator())
	}

	protected executeSqlStatement(String sqlCommand) {
		if (''.equals(sqlCommand.trim())) {
			return
		}
		try {
			executeSqlStatementInternal(sqlCommand)
		} catch (Exception e) {
			if (!isIgnoreErrors()) {
				throw new RuntimeException("Error executing ${this}. SQL statement: $sqlCommand",e)
			}
		}
	}

	protected void executeSqlStatementInternal(String sqlCommand) {
		updatedRowCount=getSql().executeUpdate(sqlCommand)
	}
	
}
