package org.disl.meta

class Column {
	String name
	Table parent
	String description
	String dataType
	String defaultValue
	boolean nullable=false

	Column(){}
	
	Column(String name,Table parent) {
		this.name=name
		this.parent=parent
	}
	
	String getColumnDefinition() {
		"${getName()} ${getDataType()}${getConstraint()}"
	}
	
	String getConstraint() {
		if (!isNullable()) {
			return " NOT NULL"
		}
		return ""
	}
	
	String toString(){
		if (parent==null || parent.getSourceAlias()==null) {
			return name
		}
		"${parent.getSourceAlias()}.${getName()}"
	}
}
