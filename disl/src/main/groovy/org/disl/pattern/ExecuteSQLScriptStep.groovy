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
