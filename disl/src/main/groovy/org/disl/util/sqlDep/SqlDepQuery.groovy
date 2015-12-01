package org.disl.util.sqlDep;

class SqlDepQuery {
	String sourceCode
	String name
	String groupName
	
	void setSourceObject(Object sourceObject) {
		name=sourceObject.getClass().getSimpleName()
		groupName=sourceObject.getClass().getPackage().getName()
	}
}