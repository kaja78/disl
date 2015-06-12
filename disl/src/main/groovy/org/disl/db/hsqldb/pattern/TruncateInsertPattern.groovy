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
