package org.disl.db.hsqldb.pattern

import java.util.List;

import org.disl.pattern.ExecuteSQLScript;
import org.disl.pattern.MappingPattern;
import org.disl.pattern.Step;

class TruncateInsertMapping extends MappingPattern {
	
	@Override
	public List<Step> getSteps() {
		return [truncateTableStep,
				insertStep];
	}
	
	protected ExecuteSQLScript getTruncateTableStep() {
		new ExecuteSQLScript(
				name: "Truncate target table",
				pattern: "TRUNCATE TABLE ${mapping.target.name}",
				sql: sql
			)
	}
	
	protected ExecuteSQLScript getInsertStep() {
		new ExecuteSQLScript(
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
