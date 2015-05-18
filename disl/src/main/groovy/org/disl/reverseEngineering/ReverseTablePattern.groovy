package org.disl.reverseEngineering


import org.disl.meta.Column
import org.disl.meta.MetaFactory
import org.disl.meta.Table
import org.disl.pattern.FileOutputStep
import org.disl.pattern.Pattern
import org.disl.pattern.Step
import org.junit.Test

class ReverseTablePattern extends Pattern {
	File outputDir=new File("src")
	String packageName
	String parentClassName
	Table table

	@Override
	public Collection<Step> getSteps() {
		File directory=new File(outputDir,packageName.replace('.', '/'))
		File file=new File(directory,"${table.name}.groovy")
		return [new FileOutputStep(name: "Groovy table definition",pattern: getCode(),file: file)];
	}

	String getCode() {
		"""\
package $packageName

import org.disl.meta.*

@Description(\"""$table.description\""")
class $table.name extends ${parentClassName} {

$columnDefinitions		
}"""
	}

	String getColumnDefinitions() {
		getTable().getColumns().collect {getColumnDefinitionCode(it)}.join("\n\n")
	}

	String getColumnDefinitionCode(Column column) {
		"""\
		@Description(\"""$column.description\""")
		@DataType("$column.dataType")
		Column $column.name"""
	}
}









