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
	
	@Test
	void testGetCommands() {
		def p=new ExecuteSQLScriptStep(commandSeparator: BACKSLASH_NEW_LINE,pattern: "A;B")
		Assert.assertEquals(1, p.getCommands().size())
		Assert.assertEquals("A;B", p.getCommands()[0])
		p=new ExecuteSQLScriptStep(pattern: "A;B");
		Assert.assertEquals(2, p.getCommands().size())
		Assert.assertEquals("A", p.getCommands()[0])
		Assert.assertEquals("B", p.getCommands()[1])
		
	}
}
