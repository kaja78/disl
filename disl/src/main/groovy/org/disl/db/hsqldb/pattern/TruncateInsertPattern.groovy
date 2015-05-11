package org.disl.db.hsqldb.pattern

import java.util.List;

import org.disl.pattern.ExecuteSQLScriptStep;
import org.disl.pattern.MappingPattern;
import org.disl.pattern.Step;

class TruncateInsertPattern extends MappingPattern {
	
	@Override
	public List<Step> getSteps() {
		return [truncateTableStep,
				insertStep];
	}
	
	protected ExecuteSQLScriptStep getTruncateTableStep() {
		new ExecuteSQLScriptStep(
				name: "Truncate target table",
				pattern: "TRUNCATE TABLE ${mapping.target.name}",
				sql: sql
			)
	}
	
	protected ExecuteSQLScriptStep getInsertStep() {
		new ExecuteSQLScriptStep(
				name: "Insert into target table",
				pattern: """/
INSERT INTO ${mapping.target.name}
	(
	${mapping.targetColumnNames.join(",\n\t")}				
	)
${mapping.SQLQuery}				
""",
				sql: sql
			)
	}
	
}
