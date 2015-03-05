package org.disl.db.hsqldb.pattern

import org.disl.pattern.ExecuteSQLScriptStep
import org.disl.pattern.Step
import org.disl.pattern.TablePattern

class CreateOrReplaceTablePattern extends TablePattern {


	@Override
	public List<Step> getSteps() {
		[dropTableStep,
		 createTableStep]
	}
	
	protected ExecuteSQLScriptStep getDropTableStep() {
	new ExecuteSQLScriptStep(
		name:"Drop table",
		pattern: "DROP TABLE ${table.name};",
		ignoreErrors: true,
		sql: getSql())
	}
	
	protected ExecuteSQLScriptStep getCreateTableStep() {
		new ExecuteSQLScriptStep(
			 name:"Create table",
			 pattern: """\
CREATE TABLE ${table.name} (
	${table.columnDefinitions.join(",\n\t")});""",
			 sql: sql)
		}

	@Override
	public String toString() {
		return "${super.toString()}(table: ${table.name})";
	}

}
