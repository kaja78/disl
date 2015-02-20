package org.disl.db.hsqldb.pattern

import org.disl.pattern.ExecuteSQLScript
import org.disl.pattern.Step
import org.disl.pattern.TablePattern

class CreateOrReplaceTable extends TablePattern {


	@Override
	public List<Step> getSteps() {
		[dropTableStep,
		 createTableStep]
	}
	
	protected ExecuteSQLScript getDropTableStep() {
	new ExecuteSQLScript(
		name:"Drop table",
		pattern: "DROP TABLE ${table.name};",
		ignoreErrors: true,
		sql: getSql())
	}
	
	protected ExecuteSQLScript getCreateTableStep() {
		new ExecuteSQLScript(
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
