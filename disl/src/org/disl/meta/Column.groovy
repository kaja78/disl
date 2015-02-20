package org.disl.meta

class Column {
	String name
	Table parent
	String description
	String dataType

	Column(){}
	
	Column(String name,Table parent) {
		this.name=name
		this.parent=parent
	}
	
	String getColumnDefinition() {
		"${getName()} ${getDataType()}"
	}
	
	String toString(){
		if (parent==null || parent.getSourceAlias()==null) {
			return name
		}
		"${parent.getSourceAlias()}.${getName()}"
	}
}
